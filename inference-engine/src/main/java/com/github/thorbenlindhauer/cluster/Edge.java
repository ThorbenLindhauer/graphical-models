package com.github.thorbenlindhauer.cluster;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.variable.Scope;

public class Edge {

  protected Cluster cluster1;
  protected Cluster cluster2;
  
  protected Scope scope;
  
  public Edge(Cluster cluster1, Cluster cluster2) {
    this.cluster1 = cluster1;
    this.cluster2 = cluster2;
    
    scope = cluster1.getScope().intersect(cluster2.getScope());
  }
  
  public Cluster getTarget(Cluster source) {
    if (source == cluster1) {
      return cluster2;
    } else if (source == cluster2) {
      return cluster1;
    } else {
      throw new ModelStructureException("Source cluster " + source + " is not part of this edge");
    }
  }
  
  public Cluster getCluster1() {
    return cluster1;
  }
  
  public Cluster getCluster2() {
    return cluster2;
  }
  
  public Scope getScope() {
    return scope;
  }
  
  public boolean connects(Cluster cluster) {
    return cluster1 == cluster || cluster2 == cluster;
  }
}
