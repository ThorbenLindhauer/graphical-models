package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.variable.Scope;

public abstract class AbstractEdge<R extends MessagePassingCluster<R, S, T>, 
  S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> implements MessagePassingEdge<R, S, T> {

  protected R cluster1;
  protected R cluster2;
  
  protected Scope scope;
  
  public AbstractEdge(R cluster1, R cluster2) {
    this.cluster1 = cluster1;
    this.cluster2 = cluster2;
    
    scope = cluster1.getScope().intersect(cluster2.getScope());
  }
  
  @Override
  public Scope getScope() {
    return scope;
  }
}
