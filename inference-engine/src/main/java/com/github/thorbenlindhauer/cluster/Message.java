package com.github.thorbenlindhauer.cluster;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

/**
 * A message is assigned to an edge and its direction is specified by the source cluster
 * 
 * @author Thorben
 */
public class Message {

  protected Cluster sourceCluster;
  protected Edge edge;
  protected DiscreteFactor potential;
  
  public Message(Cluster cluster, Edge edge) {
    if (!edge.connects(cluster)) {
      throw new ModelStructureException("Invalid message: Cluster " + cluster + " is not involved in edge " + edge);
    }
    
    this.sourceCluster = cluster;
    this.edge = edge;
  }
  
  public void update() {
    Set<Message> inMessages = new HashSet<Message>();
    Set<Edge> inEdges = sourceCluster.getInEdges(edge);
    
    for (Edge inEdge : inEdges) {
      inMessages.add(inEdge.getMessageFrom(inEdge.getConnectedCluster(sourceCluster)));
    }
    
    potential = sourceCluster.getJointFactor();
    
    // ignore null potentials
    for (Message inMessage : inMessages) {
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
  
  public Cluster getTargetCluster() {
    return edge.getConnectedCluster(sourceCluster);
  }
  
  public Cluster getSourceCluster() {
    return sourceCluster;
  }
  
  public Edge getEdge() {
    return edge;
  }
}
