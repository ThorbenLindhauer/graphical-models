package com.github.thorbenlindhauer.cluster;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.variable.Scope;

public class Edge {

  protected Scope scope;
  protected Cluster cluster1;
  /** message from cluster1 -> cluster2*/
  protected Message message1;
  
  protected Cluster cluster2;
  /** message from cluster2 -> cluster2*/
  protected Message message2;
  
  public Edge(Cluster cluster1, Cluster cluster2) {
    this.cluster1 = cluster1;
    this.message1 = new Message(cluster1, this);
    
    this.cluster2 = cluster2;
    this.message2 = new Message(cluster2, this);
    
    this.scope = cluster1.getScope().intersect(cluster2.getScope());
  }
  
  public Scope getScope() {
    return scope;
  }
  
  public Cluster getConnectedCluster(Cluster sourceCluster) {
    if (cluster1 == sourceCluster) {
      return cluster2;
    } else if (cluster2 == sourceCluster) {
      return cluster1;
    } else {
      throw new ModelStructureException("Cluster " + sourceCluster + " is not connected by this edge");
    }
  }
  
  public Message getMessageFrom(Cluster sourceCluster) {
    if (cluster1 == sourceCluster) {
      return message1;
    } else if (cluster2 == sourceCluster) {
      return message2;
    } else {
      throw new ModelStructureException("Cluster " + sourceCluster + " is not connected by this edge");
    }
  }
  
  public Message getMessageTo(Cluster targetCluster) {
    if (cluster1 == targetCluster) {
      return message2;
    } else if (cluster2 == targetCluster) {
      return message1;
    } else {
      throw new ModelStructureException("Cluster " + targetCluster + " is not connected by this edge");
    }
  }
  
  public boolean connects(Cluster cluster) {
    return cluster1 == cluster || cluster2 == cluster;
  }
}
