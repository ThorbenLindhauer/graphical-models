package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashMap;
import java.util.Map;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.exception.InferenceException;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;

public class SumProductContext implements MessagePassingContext {

  protected Map<Edge, EdgeContext> messages;
  protected Map<Cluster, DiscreteFactor> jointDistributions;
  protected Map<Cluster, DiscreteFactor> clusterPotentials;
  
  public SumProductContext(ClusterGraph clusterGraph) {
    this.messages = new HashMap<Edge, EdgeContext>();
    
    for (Edge edge : clusterGraph.getEdges()) {
      this.messages.put(edge, new EdgeContext(edge));
    }
    
    this.jointDistributions = new HashMap<Cluster, DiscreteFactor>();
    this.clusterPotentials = new HashMap<Cluster, DiscreteFactor>();
  }
  
  @Override
  public Message getMessage(Edge edge, Cluster sourceCluster) {
    EdgeContext edgeContext = messages.get(edge);
    
    if (edgeContext == null) {
      throw new InferenceException("Edge " + edge + " is not known to this context");
    }
    
    return edgeContext.getMessageFrom(sourceCluster);
  }

  @Override
  public DiscreteFactor getJointDistribution(Cluster cluster) {
    DiscreteFactor jointDistribution = jointDistributions.get(cluster);
    
    if (jointDistribution == null) {
      jointDistribution = FactorUtil.jointDistribution(cluster.getFactors());
      jointDistributions.put(cluster, jointDistribution);
    }
    
    return jointDistribution;
  }
  
  @Override
  public DiscreteFactor getClusterPotential(Cluster cluster) {
    ensurePotentialInitialized(cluster);
    return clusterPotentials.get(cluster);
  }
  
  protected void ensurePotentialInitialized(Cluster cluster) {
    DiscreteFactor potential = clusterPotentials.get(cluster);
    
    if (potential == null) {
      potential = getJointDistribution(cluster);
      
      for (Edge edge : cluster.getEdges()) {
        Message inMessage = getMessage(edge, edge.getTarget(cluster));
        DiscreteFactor messagePotential = inMessage.getPotential();
        
        // ignore null potentials
        if (potential == null) {
          potential = messagePotential;
        } else if (messagePotential != null) {
          potential = potential.product(messagePotential);
        }
      }
      
      clusterPotentials.put(cluster, potential);
    }
  }

  @Override
  public void updateClusterPotential(Cluster cluster, DiscreteFactor factor) {
    throw new UnsupportedOperationException("Cannot update potentials from outside for this context");
  }

  protected static class EdgeContext {
    protected Cluster cluster1;
    protected Message message1;
    
    protected Cluster cluster2;
    protected Message message2;
    
    public EdgeContext(Edge edge) {
      this.cluster1 = edge.getCluster1();
      this.message1 = new SumProductMessage(cluster1, edge);
      
      this.cluster2 = edge.getCluster2();
      this.message2 = new SumProductMessage(cluster2, edge);
    }
    
    public Message getMessageFrom(Cluster cluster) {
      if (cluster == cluster1) {
        return message1;
      } else if (cluster == cluster2) {
        return message2;
      } else {
        throw new ModelStructureException("");
      }
    }
  }
  

}
