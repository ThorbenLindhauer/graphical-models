package com.github.thorbenlindhauer.cluster;

import org.assertj.core.api.Condition;

public class ClusterEdgeCondition extends Condition<Edge> {

  protected Cluster cluster1;
  protected Cluster cluster2;
  
  public ClusterEdgeCondition(Cluster cluster1, Cluster cluster2) {
    this.cluster1 = cluster1;
    this.cluster2 = cluster2;
  }
  
  @Override
  public boolean matches(Edge edge) {
    return edge.connects(cluster1) && edge.connects(cluster2);
  }
}
