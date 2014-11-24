package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashMap;
import java.util.Map;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;

public class BeliefUpdateContext implements MessagePassingContext {

  protected Map<Edge, BeliefUpdateMessage> messages;
  protected Map<Cluster, DiscreteFactor> clusterPotentials;
  
  public BeliefUpdateContext(ClusterGraph clusterGraph) {
    initializeClusterPotentials(clusterGraph);
    initializeMessages(clusterGraph);
  }
  
  protected void initializeMessages(ClusterGraph clusterGraph) {
    this.messages = new HashMap<Edge, BeliefUpdateMessage>();
    for (Edge edge : clusterGraph.getEdges()) {
      BeliefUpdateMessage message = new BeliefUpdateMessage(edge);
      messages.put(edge, message);
    }
    
  }

  protected void initializeClusterPotentials(ClusterGraph clusterGraph) {
    clusterPotentials = new HashMap<Cluster, DiscreteFactor>();
    
    for (Cluster cluster : clusterGraph.getClusters()) {
      clusterPotentials.put(cluster, FactorUtil.jointDistribution(cluster.getFactors()));
    }
  }

  @Override
  public Message getMessage(Edge edge, Cluster sourceCluster) {
    BeliefUpdateMessage message = messages.get(edge);
    BeliefUpdateMessageWrapper wrapper = message.wrapAsDirectedMessage(edge.getTarget(sourceCluster));
    
    return wrapper;
  }

  @Override
  public DiscreteFactor getJointDistribution(Cluster cluster) {
    throw new UnsupportedOperationException("This context does not cache joint distributions");
  }
  
  @Override
  public DiscreteFactor getClusterPotential(Cluster cluster) {
    return clusterPotentials.get(cluster);
  }

  @Override
  public void updateClusterPotential(Cluster cluster, DiscreteFactor factor) {
    clusterPotentials.put(cluster, factor);
  }
}
