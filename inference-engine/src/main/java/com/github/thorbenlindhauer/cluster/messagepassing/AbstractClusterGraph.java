package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractClusterGraph<R extends MessagePassingCluster<R, S, T>, 
  S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> implements MessagePassingClusterGraph<R, S, T> {

  protected Set<R> clusters;
  protected Set<T> edges;
  
  public AbstractClusterGraph(Set<R> clusters) {
    this.clusters = clusters;
    this.edges = new HashSet<T>();
  }

  public Set<R> getClusters() {
    return clusters;
  }

  public Set<T> getEdges() {
    return edges;
  }
}
