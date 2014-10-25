package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;

public class BeliefPropagationClusterGraph extends AbstractClusterGraph<BeliefPropagationCluster, BeliefPropagationMessageWrapper, BeliefPropagationEdge> {

  public BeliefPropagationClusterGraph(Set<BeliefPropagationCluster> clusters) {
    super(clusters);
  }
  
  public BeliefPropagationEdge connect(BeliefPropagationCluster cluster1, BeliefPropagationCluster cluster2) {
    if (!clusters.contains(cluster1) || !clusters.contains(cluster2)) {
      throw new ModelStructureException("At least one of the cluster " + cluster1 + ", " 
          + cluster2 + " is not contained by this graph.");
    }
    
    BeliefPropagationEdge newEdge = cluster1.connectTo(cluster2);
    this.edges.add(newEdge);
    return newEdge;
  }
}
