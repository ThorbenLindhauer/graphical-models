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
package com.github.thorbenlindhauer.factorgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class FactorGraph<T extends Factor<T>> {

  protected Map<DiscreteVariable, FactorGraphNode<T>> nodes;
  protected Set<FactorGraphEdge<T>> edges;
  protected Scope scope;

  public FactorGraph(FactorGraph<T> other) {
    this(other.nodes.values(), other.edges);
  }

  public FactorGraph(Collection<FactorGraphNode<T>> nodes, Collection<FactorGraphEdge<T>> edges) {
    this.nodes = new HashMap<DiscreteVariable, FactorGraphNode<T>>();
    for (FactorGraphNode<T> node : nodes) {
      this.nodes.put(node.getVariable(), node);
    }

    this.edges = new HashSet<FactorGraphEdge<T>>(edges);
    this.scope = new Scope(this.nodes.keySet());
  }


  public Map<DiscreteVariable, FactorGraphNode<T>> getNodes() {
    return nodes;
  }

  public FactorGraphNode<T> getNode(String variableId) {
    DiscreteVariable variable = scope.getVariable(variableId);
    return nodes.get(variable);
  }

  public Set<FactorGraphEdge<T>> getEdges() {
    return edges;
  }

  public Scope getScope() {
    return scope;
  }

  public void connectPairwise(Set<FactorGraphNode<T>> nodes) {
    List<FactorGraphNode<T>> nodesAsList = new ArrayList<FactorGraphNode<T>>(nodes);

    for (int i = 0; i < nodesAsList.size(); i++) {
      FactorGraphNode<T> node1 = nodesAsList.get(i);

      for (int j = i + 1; j < nodesAsList.size(); j++) {
        FactorGraphNode<T> node2 = nodesAsList.get(j);
        FactorGraphEdge<T> edge = node1.connectTo(node2);
        this.edges.add(edge);
      }
    }
  }

  /**
   * Returns a moralized factor graph that represents the structure implicitly encoded in the factor scopes
   */
  public static <T extends Factor<T>> FactorGraph<T> fromGraphicalModel(Set<T> factors) {
    Map<DiscreteVariable, FactorGraphNode<T>> nodes = new HashMap<DiscreteVariable, FactorGraphNode<T>>();

    for (T factor : factors) {
      for (DiscreteVariable variable : factor.getVariables().getVariables()) {
        FactorGraphNode<T> node = nodes.get(variable);

        if (node == null) {
          node = new FactorGraphNode<T>(variable);
          nodes.put(variable, node);
        }

        node.addFactor(factor);
      }
    }

    FactorGraph<T> factorGraph = new FactorGraph<T>(nodes.values(), Collections.<FactorGraphEdge<T>>emptySet());

    for (T factor : factors) {
      Set<FactorGraphNode<T>> nodesForFactor = new HashSet<FactorGraphNode<T>>();

      for (DiscreteVariable variable : factor.getVariables().getVariables()) {
        nodesForFactor.add(nodes.get(variable));
      }

      factorGraph.connectPairwise(nodesForFactor);
    }

    return factorGraph;
  }
}
