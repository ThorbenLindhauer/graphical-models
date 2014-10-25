package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.exception.ModelStructureException;

public class BeliefPropagationEdge extends AbstractEdge<BeliefPropagationCluster, BeliefPropagationMessageWrapper, BeliefPropagationEdge> {

  protected BeliefPropagationMessage message;
  
  // cluster1 -> cluster2
  protected BeliefPropagationMessageWrapper message1Wrapper;
  
  //cluster2 -> cluster1
  protected BeliefPropagationMessageWrapper message2Wrapper;
  
  public BeliefPropagationEdge(BeliefPropagationCluster cluster1, BeliefPropagationCluster cluster2) {
    super(cluster1, cluster2);
    
    this.message = new BeliefPropagationMessage(this);
    this.message1Wrapper = message.wrapAsDirectedMessage(cluster2);
    this.message2Wrapper = message.wrapAsDirectedMessage(cluster1);
  }
  
  @Override
  public BeliefPropagationMessageWrapper getMessageFrom(BeliefPropagationCluster cluster) {
    if (cluster1 == cluster) {
      return message1Wrapper;
    } else if (cluster2 == cluster) {
      return message2Wrapper;
    } else {
      throw new ModelStructureException("Cluster " + cluster + " is not connected by this edge");
    }
  }

  @Override
  public BeliefPropagationMessageWrapper getMessageTo(BeliefPropagationCluster cluster) {
    if (cluster1 == cluster) {
      return message2Wrapper;
    } else if (cluster2 == cluster) {
      return message1Wrapper;
    } else {
      throw new ModelStructureException("Cluster " + cluster + " is not connected by this edge");
    }
  }
  
  public BeliefPropagationCluster getConnectedCluster(BeliefPropagationCluster sourceCluster) {
    if (cluster1 == sourceCluster) {
      return cluster2;
    } else if (cluster2 == sourceCluster) {
      return cluster1;
    } else {
      throw new ModelStructureException("Cluster " + sourceCluster + " is not connected by this edge");
    }
  }

}
