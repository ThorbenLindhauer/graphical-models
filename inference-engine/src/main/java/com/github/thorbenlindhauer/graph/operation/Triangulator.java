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
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraphNode;

public class Triangulator {

  /**
   * Returns the graph induced by the provided elimination ordering. Such a graph is always triangulated.
   */
  public FactorGraph getInducedGraph(FactorGraph graph, List<String> variableEliminationOrder) {
    FactorGraph inducedGraph = new FactorGraph(graph);
    Set<FactorGraphNode> eliminatedNodes = new HashSet<FactorGraphNode>();
    
    for (String eliminatedVariable : variableEliminationOrder) {
      FactorGraphNode eliminatedNode = inducedGraph.getNode(eliminatedVariable);
      eliminatedNodes.add(eliminatedNode);
      
      if (eliminatedNode == null) {
        throw new ModelStructureException("Variable " + eliminatedVariable + " is not part of this factor graph");
      }
      
      Set<FactorGraphNode> neighbours = eliminatedNode.getNeighbours();
      
      // do not connect nodes that have already been eliminated
      neighbours.removeAll(eliminatedNodes);
      
      inducedGraph.connectPairwise(neighbours);
    }
    
    return inducedGraph;
  }
}
