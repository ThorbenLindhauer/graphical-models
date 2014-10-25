package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.exception.ModelStructureException;

public class SumProductEdge extends AbstractEdge<SumProductCluster, SumProductMessage, SumProductEdge> {

  /** message from cluster1 -> cluster2*/
  protected SumProductMessage message1;
  
  /** message from cluster2 -> cluster2*/
  protected SumProductMessage message2;
  
  public SumProductEdge(SumProductCluster cluster1, SumProductCluster cluster2) {
    super(cluster1, cluster2);
    this.message1 = new SumProductMessage(cluster1, this);
    this.message2 = new SumProductMessage(cluster2, this);
    
    this.scope = cluster1.getScope().intersect(cluster2.getScope());
  }
  
  public SumProductCluster getConnectedCluster(SumProductCluster sourceCluster) {
    if (cluster1 == sourceCluster) {
      return cluster2;
    } else if (cluster2 == sourceCluster) {
      return cluster1;
    } else {
      throw new ModelStructureException("Cluster " + sourceCluster + " is not connected by this edge");
    }
  }
  
  public SumProductMessage getMessageFrom(SumProductCluster sourceCluster) {
    if (cluster1 == sourceCluster) {
      return message1;
    } else if (cluster2 == sourceCluster) {
      return message2;
    } else {
      throw new ModelStructureException("Cluster " + sourceCluster + " is not connected by this edge");
    }
  }
  
  public SumProductMessage getMessageTo(SumProductCluster targetCluster) {
    if (cluster1 == targetCluster) {
      return message2;
    } else if (cluster2 == targetCluster) {
      return message1;
    } else {
      throw new ModelStructureException("Cluster " + targetCluster + " is not connected by this edge");
    }
  }
  
  public boolean connects(SumProductCluster cluster) {
    return cluster1 == cluster || cluster2 == cluster;
  }
}
