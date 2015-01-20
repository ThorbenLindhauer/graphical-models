/* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
public class CliqueTreeInferencer implements DiscreteModelInferencer {

  protected ClusterGraph<DiscreteFactor> clusterGraph;
  protected Cluster<DiscreteFactor> rootCluster;
  protected boolean messagesPropagated = false;
  protected MessagePassingContext<DiscreteFactor> messagePassingContext;

  public CliqueTreeInferencer(ClusterGraph<DiscreteFactor> clusterGraph, Cluster<DiscreteFactor> rootCluster, MessagePassingContextFactory messageContextFactory) {
    this.clusterGraph = clusterGraph;
    this.rootCluster = rootCluster;
    this.messagePassingContext = messageContextFactory.newMessagePassingContext(clusterGraph);
  }

  public double jointProbability(Scope projection, int[] variableAssignment) {
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(projection);
    return matchingFactor.marginal(projection).normalize().getValueForAssignment(variableAssignment);
  }

  public double jointProbability(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Scope jointScope = projection.union(observedVariables);
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(jointScope);
    return matchingFactor.normalize().observation(observedVariables, observation).marginal(projection).getValueForAssignment(variableAssignment);
  }

  public double jointProbabilityConditionedOn(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Scope jointScope = projection.union(observedVariables);
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(jointScope);
    return matchingFactor.observation(observedVariables, observation).marginal(projection).normalize().getValueForAssignment(variableAssignment);
  }

  protected DiscreteFactor getClusterFactorContainingScope(Scope scope) {
    ensureMessagesPropagated();

    for (Cluster<DiscreteFactor> cluster : clusterGraph.getClusters()) {
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
    Set<Message<DiscreteFactor>> initialForwardMessages = new HashSet<Message<DiscreteFactor>>();
    for (Cluster<DiscreteFactor> cluster : clusterGraph.getClusters()) {
      // determine the initial messages
      if (cluster != rootCluster && cluster.getEdges().size() == 1) {
        Edge<DiscreteFactor> outEdge = cluster.getEdges().iterator().next();
        initialForwardMessages.add(messagePassingContext.getMessage(outEdge, cluster));
      }
    }

    executeMessagePass(initialForwardMessages, true);

    // backward pass (beginning at root)
    Set<Message<DiscreteFactor>> initialBackwardMessages = new HashSet<Message<DiscreteFactor>>();
    for (Edge<DiscreteFactor> rootOutEdge : rootCluster.getEdges()) {
      initialBackwardMessages.add(messagePassingContext.getMessage(rootOutEdge, rootCluster));
    }

    executeMessagePass(initialBackwardMessages, false);

    messagesPropagated = true;
  }

  protected void executeMessagePass(Set<Message<DiscreteFactor>> initialMessages, boolean isForwardPass) {
    Set<Edge<DiscreteFactor>> processedEdges = new HashSet<Edge<DiscreteFactor>>();
    Set<Message<DiscreteFactor>> currentMessages = initialMessages;

    while (!currentMessages.isEmpty()) {
      Iterator<Message<DiscreteFactor>> it = currentMessages.iterator();
      Message<DiscreteFactor> currentMessage = it.next();
      it.remove();

      currentMessage.update(messagePassingContext);
      processedEdges.add(currentMessage.getEdge());

      Cluster<DiscreteFactor> targetCluster = currentMessage.getTargetCluster();
      if (targetCluster != rootCluster) {
        Set<Edge<DiscreteFactor>> targetOutEdges = targetCluster.getOtherEdges(currentMessage.getEdge());

        for (Edge<DiscreteFactor> targetOutEdge : targetOutEdges) {
          // Only add the message for the out edge, if it has not yet been computed in this message pass.
          // If this is a forward pass (i.e. the first pass), we additionally need to check
          // whether the incoming messages are already all available (ie. the candidate out message has no more pending in messages)
          if (!processedEdges.contains(targetOutEdge) &&
              (!isForwardPass || processedEdges.containsAll(targetCluster.getOtherEdges(targetOutEdge)))) {
            currentMessages.add(messagePassingContext.getMessage(targetOutEdge, targetCluster));
          }
        }
      }
    }
  }

}
