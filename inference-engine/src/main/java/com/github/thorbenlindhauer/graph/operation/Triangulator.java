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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraphNode;

public class Triangulator {

  /**
   * Returns the graph induced by the provided elimination ordering. Such a graph is always triangulated.
   */
  public <T extends Factor<T>> FactorGraph<T> getInducedGraph(FactorGraph<T> graph, List<String> variableEliminationOrder) {
    FactorGraph<T> inducedGraph = new FactorGraph<T>(graph);
    Set<FactorGraphNode<T>> eliminatedNodes = new HashSet<FactorGraphNode<T>>();

    for (String eliminatedVariable : variableEliminationOrder) {
      FactorGraphNode<T> eliminatedNode = inducedGraph.getNode(eliminatedVariable);
      eliminatedNodes.add(eliminatedNode);

      if (eliminatedNode == null) {
        throw new ModelStructureException("Variable " + eliminatedVariable + " is not part of this factor graph");
      }

      Set<FactorGraphNode<T>> neighbours = eliminatedNode.getNeighbours();

      // do not connect nodes that have already been eliminated
      neighbours.removeAll(eliminatedNodes);

      inducedGraph.connectPairwise(neighbours);
    }

    return inducedGraph;
  }
}
