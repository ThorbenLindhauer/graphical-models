package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashMap;
import java.util.Map;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;

public class BeliefPropagationContext implements MessagePassingContext {

  protected Map<Edge, BeliefPropagationMessage> messages;
  protected Map<Cluster, DiscreteFactor> clusterPotentials;
  
  public BeliefPropagationContext(ClusterGraph clusterGraph) {
    initializeClusterPotentials(clusterGraph);
    initializeMessages(clusterGraph);
  }
  
  protected void initializeMessages(ClusterGraph clusterGraph) {
    this.messages = new HashMap<Edge, BeliefPropagationMessage>();
    for (Edge edge : clusterGraph.getEdges()) {
      BeliefPropagationMessage message = new BeliefPropagationMessage(edge);
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
    BeliefPropagationMessage message = messages.get(edge);
    BeliefPropagationMessageWrapper wrapper = message.wrapAsDirectedMessage(edge.getTarget(sourceCluster));
    
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
