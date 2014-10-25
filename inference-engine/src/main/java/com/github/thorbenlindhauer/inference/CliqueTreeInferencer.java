package com.github.thorbenlindhauer.inference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.messagepassing.Message;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingCluster;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingEdge;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

//TODO: think about interface "IncrementalInferencer" that allows to submit observations incrementally
public class CliqueTreeInferencer<R extends MessagePassingCluster<R, S, T>, 
S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> implements ExactInferencer {
  
  protected MessagePassingClusterGraph<R, S, T> clusterGraph;
  protected R rootCluster;
  protected boolean messagesPropagated = false;
  
  public CliqueTreeInferencer(MessagePassingClusterGraph<R, S, T> clusterGraph, R rootCluster) {
    this.clusterGraph = clusterGraph;
    this.rootCluster = rootCluster;
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
    
    for (R cluster : clusterGraph.getClusters()) {
      if (cluster.getScope().contains(scope)) {
        return cluster.getPotential().marginal(scope);
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
    Set<S> initialForwardMessages = new HashSet<S>();
    for (R cluster : clusterGraph.getClusters()) {
      // determine the initial messages
      if (cluster != rootCluster && cluster.getEdges().size() == 1) {
        T outEdge = cluster.getEdges().iterator().next();
        initialForwardMessages.add(outEdge.getMessageFrom(cluster));
      }
    }
    
    executeMessagePass(initialForwardMessages);
    
    // backward pass (beginning at root)
    Set<S> initialBackwardMessages = new HashSet<S>();
    for (T rootOutEdge : rootCluster.getEdges()) {
      initialBackwardMessages.add(rootOutEdge.getMessageFrom(rootCluster));
    }
    
    executeMessagePass(initialBackwardMessages);
    
    messagesPropagated = true;
  }
  
  protected void executeMessagePass(Set<S> initialMessages) {
    Set<T> processedEdges = new HashSet<T>();
    Set<S> currentMessages = initialMessages;
    
    while (!currentMessages.isEmpty()) {
      Iterator<S> it = currentMessages.iterator();
      S currentMessage = it.next();
      it.remove();
      
      currentMessage.update();
      processedEdges.add(currentMessage.getEdge());
      
      R targetCluster = currentMessage.getTargetCluster();
      if (targetCluster != rootCluster) {
        Set<T> targetOutEdges = targetCluster.getOtherEdges(currentMessage.getEdge());
        
        for (T targetOutEdge : targetOutEdges) {
          // only add the message for the out edge, if it has not yet been computed and it can be computed right away
          // (ie. has no more pending in messages)
          if (!processedEdges.contains(targetOutEdge) && 
              processedEdges.containsAll(targetCluster.getOtherEdges(targetOutEdge))) {
            currentMessages.add(targetOutEdge.getMessageFrom(targetCluster));
          }
        }
      }
    }
  }
  
}
