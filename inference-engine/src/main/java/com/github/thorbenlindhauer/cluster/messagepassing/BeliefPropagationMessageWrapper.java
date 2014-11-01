package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;


/**
 * A view on {@link BeliefPropagationMessage} that provides a target cluster
 * 
 * @author Thorben
 */
public class BeliefPropagationMessageWrapper implements Message {

  protected BeliefPropagationMessage message;
  protected Cluster targetCluster;
  
  public BeliefPropagationMessageWrapper(BeliefPropagationMessage message, Cluster targetCluster) {
    this.message = message;
    this.targetCluster = targetCluster;
  }
  
  @Override
  public Edge getEdge() {
    return message.getEdge();
  }

  @Override
  public Cluster getTargetCluster() {
    return targetCluster;
  }

  @Override
  public Cluster getSourceCluster() {
    return getEdge().getTarget(targetCluster);
  }

  @Override
  public void update(MessagePassingContext messagePassingContext) {
    message.update(messagePassingContext, getSourceCluster(), targetCluster);
  }

  @Override
  public DiscreteFactor getPotential() {
    return message.getPotential();
  }
}
