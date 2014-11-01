package com.github.thorbenlindhauer.inference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.cluster.messagepassing.Message;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContext;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

//TODO: think about interface "IncrementalInferencer" that allows to submit observations incrementally
public class CliqueTreeInferencer implements ExactInferencer {
  
  protected ClusterGraph clusterGraph;
  protected Cluster rootCluster;
  protected boolean messagesPropagated = false;
  protected MessagePassingContext messagePassingContext;
  
  public CliqueTreeInferencer(ClusterGraph clusterGraph, Cluster rootCluster, MessagePassingContextFactory messageContextFactory) {
    this.clusterGraph = clusterGraph;
    this.rootCluster = rootCluster;
    this.messagePassingContext = messageContextFactory.newMessagePassingContext(clusterGraph);
  }

  public double jointProbability(Scope projection, int[] variableAssignment) {
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(projection);
    return matchingFactor.marginal(projection).getValueForAssignment(variableAssignment);
  }

  public double jointProbability(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Scope jointScope = projection.union(observedVariables);
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(jointScope);
    return matchingFactor.observation(observedVariables, observation).marginal(projection).getValueForAssignment(variableAssignment);
  }

  public double jointProbabilityConditionedOn(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Scope jointScope = projection.union(observedVariables);
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(jointScope);
    return matchingFactor.observation(observedVariables, observation).marginal(projection).normalize().getValueForAssignment(variableAssignment);
  }
  
  protected DiscreteFactor getClusterFactorContainingScope(Scope scope) {
    ensureMessagesPropagated();
    
    for (Cluster cluster : clusterGraph.getClusters()) {
      if (cluster.getScope().contains(scope)) {
        return messagePassingContext.getClusterPotential(cluster).marginal(scope);
      }
    }
    
    throw new ModelStructureException("There is no cluster that contains scope " + scope + " entirely and "
        + "queries spanning multiple clusters are not yet implemented");
  }
  
  protected void ensureMessagesPropagated() {
    if (!messagesPropagated) {
      propagateMessages();
    }
  }
  
  protected void propagateMessages() {
    // forward pass (beginning at leaves)
    Set<Message> initialForwardMessages = new HashSet<Message>();
    for (Cluster cluster : clusterGraph.getClusters()) {
      // determine the initial messages
      if (cluster != rootCluster && cluster.getEdges().size() == 1) {
        Edge outEdge = cluster.getEdges().iterator().next();
        initialForwardMessages.add(messagePassingContext.getMessage(outEdge, cluster));
      }
    }
    
    executeMessagePass(initialForwardMessages);
    
    // backward pass (beginning at root)
    Set<Message> initialBackwardMessages = new HashSet<Message>();
    for (Edge rootOutEdge : rootCluster.getEdges()) {
      initialBackwardMessages.add(messagePassingContext.getMessage(rootOutEdge, rootCluster));
    }
    
    executeMessagePass(initialBackwardMessages);
    
    messagesPropagated = true;
  }
  
  protected void executeMessagePass(Set<Message> initialMessages) {
    Set<Edge> processedEdges = new HashSet<Edge>();
    Set<Message> currentMessages = initialMessages;
    
    while (!currentMessages.isEmpty()) {
      Iterator<Message> it = currentMessages.iterator();
      Message currentMessage = it.next();
      it.remove();
      
      currentMessage.update(messagePassingContext);
      processedEdges.add(currentMessage.getEdge());
      
      Cluster targetCluster = currentMessage.getTargetCluster();
      if (targetCluster != rootCluster) {
        Set<Edge> targetOutEdges = targetCluster.getOtherEdges(currentMessage.getEdge());
        
        for (Edge targetOutEdge : targetOutEdges) {
          // only add the message for the out edge, if it has not yet been computed and it can be computed right away
          // (ie. has no more pending in messages)
          if (!processedEdges.contains(targetOutEdge) && 
              processedEdges.containsAll(targetCluster.getOtherEdges(targetOutEdge))) {
            currentMessages.add(messagePassingContext.getMessage(targetOutEdge, targetCluster));
          }
        }
      }
    }
  }
  
}
