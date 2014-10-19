package com.github.thorbenlindhauer.cluster;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;

public class ClusterGraph {

  protected Set<Cluster> clusters;
  protected Set<Edge> edges;
  
  public ClusterGraph(Set<Cluster> clusters) {
    this.clusters = clusters;
    this.edges = new HashSet<Edge>();
  }

  public Set<Cluster> getClusters() {
    return clusters;
  }

  public Set<Edge> getEdges() {
    return edges;
  }
  
  public Edge connect(Cluster cluster1, Cluster cluster2) {
    if (!clusters.contains(cluster1) || !clusters.contains(cluster2)) {
      throw new ModelStructureException("At least one of the cluster " + cluster1 + ", " 
          + cluster2 + " is not contained by this graph.");
    }
    
    Edge newEdge = cluster1.connectTo(cluster2);
    this.edges.add(newEdge);
    return newEdge;
  }
}
