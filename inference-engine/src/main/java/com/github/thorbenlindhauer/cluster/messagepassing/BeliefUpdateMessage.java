package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

public class BeliefUpdateMessage {

  protected DiscreteFactor potential;
  protected Edge edge;

  public BeliefUpdateMessage(Edge edge) {
    this.edge = edge;
  }
  
  public void update(MessagePassingContext messagePassingContext, Cluster sourceCluster, Cluster targetCluster) {
    DiscreteFactor newPotential = messagePassingContext.getClusterPotential(sourceCluster).marginal(edge.getScope());
    
    DiscreteFactor targetUpdate = newPotential;
    if (potential != null) {
      targetUpdate = newPotential.division(potential);
    }

    DiscreteFactor newTargetPotential = targetUpdate;
    
    DiscreteFactor targetPotential = messagePassingContext.getClusterPotential(targetCluster);
    if (targetPotential != null) {
      newTargetPotential = newTargetPotential.product(targetPotential);
    }
    
    messagePassingContext.updateClusterPotential(targetCluster, newTargetPotential);
    
    potential = newPotential;
  }

  public BeliefUpdateMessageWrapper wrapAsDirectedMessage(Cluster targetCluster) {
    return new BeliefUpdateMessageWrapper(this, targetCluster);
  }
  
  public Edge getEdge() {
    return edge;
  }
  
  public DiscreteFactor getPotential() {
    return potential;
  }
}
