package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

/**
 * A message is assigned to an edge and its direction is specified by the source cluster
 * 
 * @author Thorben
 */
public class SumProductMessage extends AbstractMessage<SumProductCluster, SumProductMessage, SumProductEdge> {

  protected SumProductCluster sourceCluster;
  protected DiscreteFactor potential;
  
  public SumProductMessage(SumProductCluster cluster, SumProductEdge edge) {
    super(edge);
    if (!edge.connects(cluster)) {
      throw new ModelStructureException("Invalid message: Cluster " + cluster + " is not involved in edge " + edge);
    }
    
    this.sourceCluster = cluster;
    this.edge = edge;
  }
  
  public void update() {
    Set<SumProductMessage> inMessages = new HashSet<SumProductMessage>();
    Set<SumProductEdge> inEdges = sourceCluster.getOtherEdges(edge);
    
    for (SumProductEdge inEdge : inEdges) {
      inMessages.add(inEdge.getMessageFrom(inEdge.getConnectedCluster(sourceCluster)));
    }
    
    potential = sourceCluster.getJointFactor();
    
    // ignore null potentials
    for (SumProductMessage inMessage : inMessages) {
      if (potential == null) {
        potential = inMessage.potential;
      } else if (inMessage.potential != null) {
        potential = potential.product(inMessage.potential);
      }
    }
    
    if (potential != null) {
      potential = potential.marginal(edge.getScope());
    }
    
  }
  
  public DiscreteFactor getPotential() {
    return potential;
  }
  
  public SumProductCluster getTargetCluster() {
    return edge.getConnectedCluster(sourceCluster);
  }
  
  public SumProductCluster getSourceCluster() {
    return sourceCluster;
  }
}
