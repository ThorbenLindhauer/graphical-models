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
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraphNode;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class MaximumCardinalityCliqueOperation implements FactorGraphOperation<Set<Cluster>> {

  @Override
  public Set<Cluster> execute(FactorGraph factorGraph) {
    if (factorGraph.getNodes().isEmpty()) {
      return Collections.emptySet();
    }
    
    Set<FactorGraphNode> currentClique = new HashSet<FactorGraphNode>();
    Set<Cluster> clusters = new HashSet<Cluster>();
    MarkingContext context = new MarkingContext();
    Set<DiscreteFactor> assignedFactors = new HashSet<DiscreteFactor>();
    
    // initially mark random node
    FactorGraphNode nodeToMark = factorGraph.getNodes().values().iterator().next();

    while (nodeToMark != null) {
      context.mark(nodeToMark);
      
      boolean fullyConnectedToCurrentClique = true;
      for (FactorGraphNode currentCliqueNode : currentClique) {
        if (!currentCliqueNode.isConnectedTo(nodeToMark)) {
          fullyConnectedToCurrentClique = false;
        }
      }
      
      if (fullyConnectedToCurrentClique) {
        currentClique.add(nodeToMark);
      } else {
        Cluster cluster = clusterFromNodes(currentClique, assignedFactors);
        clusters.add(cluster);
        currentClique = new HashSet<FactorGraphNode>();
        currentClique.add(nodeToMark);
        for (FactorGraphNode neighbourNode : nodeToMark.getNeighbours()) {
          if (context.isMarked(neighbourNode)) {
            currentClique.add(neighbourNode);
          }
        }
        
      }
      
      nodeToMark = context.getNodeWithMostMarkedNeighbours();
    }
    
    if (!currentClique.isEmpty()) {
      Cluster cluster = clusterFromNodes(currentClique, assignedFactors);
      clusters.add(cluster);
    }
    
    return clusters;
  }
  

  protected Cluster clusterFromNodes(Set<FactorGraphNode> currentClique, Set<DiscreteFactor> assignedFactors) {
    Set<DiscreteVariable> variables = new HashSet<DiscreteVariable>();
    
    for (FactorGraphNode node : currentClique) {
      variables.add(node.getVariable());
    }
    
    Scope scope = new Scope(variables);
    
    Set<DiscreteFactor> factors = new HashSet<DiscreteFactor>();
    
    for (FactorGraphNode node : currentClique) {
      for (DiscreteFactor factor : node.getFactors()) {
        if (!assignedFactors.contains(factor) && scope.contains(factor.getVariables())) {
          factors.add(factor);
          assignedFactors.add(factor);
        }
      }
    }
    
    return new Cluster(scope, factors);
  }


  protected boolean connectedToAll(FactorGraphNode node, Set<FactorGraphNode> nodes) {
    for (FactorGraphNode otherNode : nodes) {
      if (!node.getEdges().containsKey(otherNode.getVariable())) {
        return false;
      }
    }
    
    return true;
  }
  
  public static class MarkingContext {
    protected Set<FactorGraphNode> markedNodes;
    protected Map<FactorGraphNode, Integer> neighbourMarkings;
    
    public MarkingContext() {
      this.markedNodes = new HashSet<FactorGraphNode>();
      this.neighbourMarkings = new HashMap<FactorGraphNode, Integer>();
    }
    
    public void mark(FactorGraphNode node) {
      boolean newNode = markedNodes.add(node);
      neighbourMarkings.remove(node);
      if (newNode) {
        for (FactorGraphNode neighbour : node.getNeighbours()) {
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
    
    public FactorGraphNode getNodeWithMostMarkedNeighbours() {
      FactorGraphNode node = null;
      Integer highestMarking = 0;
      
      for (Map.Entry<FactorGraphNode, Integer> marking : neighbourMarkings.entrySet()) {
        if (marking.getValue() > highestMarking) {
          node = marking.getKey();
          highestMarking = marking.getValue();
        }
      }
      
      return node;
    }
    
    public boolean isMarked(FactorGraphNode node) {
      return markedNodes.contains(node);
    }
  }
  

}
