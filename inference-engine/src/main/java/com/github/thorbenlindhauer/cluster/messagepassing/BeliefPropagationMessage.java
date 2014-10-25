package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.factor.DiscreteFactor;

public class BeliefPropagationMessage {

  protected DiscreteFactor potential;
  protected BeliefPropagationEdge edge;

  public BeliefPropagationMessage(BeliefPropagationEdge edge) {
    this.edge = edge;
  }
  
  public void update(BeliefPropagationCluster sourceCluster, BeliefPropagationCluster targetCluster) {
    DiscreteFactor newPotential = sourceCluster.getPotential().marginal(edge.getScope());
    
    DiscreteFactor targetUpdate = newPotential;
    if (potential != null) {
      targetUpdate = newPotential.division(potential);
    }
    
    targetCluster.updatePotential(targetUpdate);
    
    potential = newPotential;
  }

  public BeliefPropagationMessageWrapper wrapAsDirectedMessage(BeliefPropagationCluster targetCluster) {
    return new BeliefPropagationMessageWrapper(this, targetCluster);
  }
  
  public BeliefPropagationEdge getEdge() {
    return edge;
  }
  
  

}
