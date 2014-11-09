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
