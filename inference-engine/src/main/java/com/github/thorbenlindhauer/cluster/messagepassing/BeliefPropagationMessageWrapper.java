package com.github.thorbenlindhauer.cluster.messagepassing;


/**
 * A view on {@link BeliefPropagationMessage} that provides a target cluster
 * 
 * @author Thorben
 */
public class BeliefPropagationMessageWrapper extends AbstractMessage<BeliefPropagationCluster, BeliefPropagationMessageWrapper, BeliefPropagationEdge> {

  protected BeliefPropagationMessage message;
  protected BeliefPropagationCluster targetCluster;
  
  public BeliefPropagationMessageWrapper(BeliefPropagationMessage message, BeliefPropagationCluster targetCluster) {
    super(null);
    this.message = message;
    this.targetCluster = targetCluster;
  }
  
  @Override
  public void update() {
    message.update(getSourceCluster(), getTargetCluster());
  }

  @Override
  public BeliefPropagationEdge getEdge() {
    return message.getEdge();
  }

  @Override
  public BeliefPropagationCluster getTargetCluster() {
    return targetCluster;
  }

  @Override
  public BeliefPropagationCluster getSourceCluster() {
    return getEdge().getConnectedCluster(targetCluster);
  }
}
