package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;

public class SumProductClusterGraph extends AbstractClusterGraph<SumProductCluster, SumProductMessage, SumProductEdge> {

  public SumProductClusterGraph(Set<SumProductCluster> clusters) {
    super(clusters);
  }

  public SumProductEdge connect(SumProductCluster cluster1, SumProductCluster cluster2) {
    if (!clusters.contains(cluster1) || !clusters.contains(cluster2)) {
      throw new ModelStructureException("At least one of the cluster " + cluster1 + ", " 
          + cluster2 + " is not contained by this graph.");
    }
    
    SumProductEdge newEdge = cluster1.connectTo(cluster2);
    this.edges.add(newEdge);
    return newEdge;
  }
}
