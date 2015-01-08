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
package com.github.thorbenlindhauer.graph.operation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraphNode;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class MaximumCardinalityCliqueOperation<T extends Factor<T>> implements FactorGraphOperation<Set<Cluster<T>>, T> {

  @Override
  public Set<Cluster<T>> execute(FactorGraph<T> factorGraph) {
    if (factorGraph.getNodes().isEmpty()) {
      return Collections.emptySet();
    }

    Set<FactorGraphNode<T>> currentClique = new HashSet<FactorGraphNode<T>>();
    Set<Cluster<T>> clusters = new HashSet<Cluster<T>>();
    MarkingContext<T> context = new MarkingContext<T>();
    Set<T> assignedFactors = new HashSet<T>();

    // initially mark random node
    FactorGraphNode<T> nodeToMark = factorGraph.getNodes().values().iterator().next();

    while (nodeToMark != null) {
      context.mark(nodeToMark);

      boolean fullyConnectedToCurrentClique = true;
      for (FactorGraphNode<T> currentCliqueNode : currentClique) {
        if (!currentCliqueNode.isConnectedTo(nodeToMark)) {
          fullyConnectedToCurrentClique = false;
        }
      }

      if (fullyConnectedToCurrentClique) {
        currentClique.add(nodeToMark);
      } else {
        Cluster<T> cluster = clusterFromNodes(currentClique, assignedFactors);
        clusters.add(cluster);
        currentClique = new HashSet<FactorGraphNode<T>>();
        currentClique.add(nodeToMark);
        for (FactorGraphNode<T> neighbourNode : nodeToMark.getNeighbours()) {
          if (context.isMarked(neighbourNode)) {
            currentClique.add(neighbourNode);
          }
        }

      }

      nodeToMark = context.getNodeWithMostMarkedNeighbours();
    }

    if (!currentClique.isEmpty()) {
      Cluster<T> cluster = clusterFromNodes(currentClique, assignedFactors);
      clusters.add(cluster);
    }

    return clusters;
  }


  protected Cluster<T> clusterFromNodes(Set<FactorGraphNode<T>> currentClique, Set<T> assignedFactors) {
    Set<DiscreteVariable> variables = new HashSet<DiscreteVariable>();

    for (FactorGraphNode<T> node : currentClique) {
      variables.add(node.getVariable());
    }

    Scope scope = new Scope(variables);

    Set<T> factors = new HashSet<T>();

    for (FactorGraphNode<T> node : currentClique) {
      for (T factor : node.getFactors()) {
        if (!assignedFactors.contains(factor) && scope.contains(factor.getVariables())) {
          factors.add(factor);
          assignedFactors.add(factor);
        }
      }
    }

    return new Cluster<T>(scope, factors);
  }


  protected boolean connectedToAll(FactorGraphNode<?> node, Set<FactorGraphNode<?>> nodes) {
    for (FactorGraphNode<?> otherNode : nodes) {
      if (!node.getEdges().containsKey(otherNode.getVariable())) {
        return false;
      }
    }

    return true;
  }

  public static class MarkingContext<T extends Factor<T>> {
    protected Set<FactorGraphNode<T>> markedNodes;
    protected Map<FactorGraphNode<T>, Integer> neighbourMarkings;

    public MarkingContext() {
      this.markedNodes = new HashSet<FactorGraphNode<T>>();
      this.neighbourMarkings = new HashMap<FactorGraphNode<T>, Integer>();
    }

    public void mark(FactorGraphNode<T> node) {
      boolean newNode = markedNodes.add(node);
      neighbourMarkings.remove(node);
      if (newNode) {
        for (FactorGraphNode<T> neighbour : node.getNeighbours()) {
          if (!markedNodes.contains(neighbour)) {
            if (neighbourMarkings.containsKey(neighbour)) {
              neighbourMarkings.put(neighbour, neighbourMarkings.get(neighbour) + 1);
            } else {
              neighbourMarkings.put(neighbour, 1);
            }
          }
        }
      }
    }

    public FactorGraphNode<T> getNodeWithMostMarkedNeighbours() {
      FactorGraphNode<T> node = null;
      Integer highestMarking = 0;

      for (Map.Entry<FactorGraphNode<T>, Integer> marking : neighbourMarkings.entrySet()) {
        if (marking.getValue() > highestMarking) {
          node = marking.getKey();
          highestMarking = marking.getValue();
        }
      }

      return node;
    }

    public boolean isMarked(FactorGraphNode<T> node) {
      return markedNodes.contains(node);
    }
  }


}
