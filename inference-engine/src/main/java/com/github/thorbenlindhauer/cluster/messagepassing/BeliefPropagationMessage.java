package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

public class BeliefPropagationMessage {

  protected DiscreteFactor potential;
  protected Edge edge;

  public BeliefPropagationMessage(Edge edge) {
    this.edge = edge;
  }
  
  public void update(MessagePassingContext messagePassingContext, Cluster sourceCluster, Cluster targetCluster) {
    DiscreteFactor newPotential = messagePassingContext.getClusterPotential(sourceCluster).marginal(edge.getScope());
    
    DiscreteFactor targetUpdate = newPotential;
    if (potential != null) {
      targetUpdate = newPotential.division(potential);
    }
    
    DiscreteFactor targetPotential = messagePassingContext.getClusterPotential(targetCluster);
    DiscreteFactor newTargetPotential = targetPotential.product(targetUpdate);
    messagePassingContext.updateClusterPotential(targetCluster, newTargetPotential);
    
    potential = newPotential;
  }

  public BeliefPropagationMessageWrapper wrapAsDirectedMessage(Cluster targetCluster) {
    return new BeliefPropagationMessageWrapper(this, targetCluster);
  }
  
  public Edge getEdge() {
    return edge;
  }
  
  public DiscreteFactor getPotential() {
    return potential;
  }
}
