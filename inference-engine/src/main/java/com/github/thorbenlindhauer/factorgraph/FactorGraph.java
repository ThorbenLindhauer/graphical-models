package com.github.thorbenlindhauer.factorgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class FactorGraph {

  protected Map<DiscreteVariable, FactorGraphNode> nodes;
  protected Set<FactorGraphEdge> edges;
  protected Scope scope;
  
  public FactorGraph(FactorGraph other) {
    this(other.nodes.values(), other.edges);
  }
  
  public FactorGraph(Collection<FactorGraphNode> nodes, Collection<FactorGraphEdge> edges) {
    this.nodes = new HashMap<DiscreteVariable, FactorGraphNode>();
    for (FactorGraphNode node : nodes) {
      this.nodes.put(node.getVariable(), node);
    }
    
    this.edges = new HashSet<FactorGraphEdge>(edges);
    this.scope = new Scope(this.nodes.keySet());
  }
  

  public Map<DiscreteVariable, FactorGraphNode> getNodes() {
    return nodes;
  }
  
  public FactorGraphNode getNode(String variableId) {
    DiscreteVariable variable = scope.getVariable(variableId);
    return nodes.get(variable);
  }

  public Set<FactorGraphEdge> getEdges() {
    return edges;
  }
  
  public Scope getScope() {
    return scope;
  }
  
  public void connectPairwise(Set<FactorGraphNode> nodes) {
    List<FactorGraphNode> nodesAsList = new ArrayList<FactorGraphNode>(nodes);
    
    for (int i = 0; i < nodesAsList.size(); i++) {
      FactorGraphNode node1 = nodesAsList.get(i);
      
      for (int j = i + 1; j < nodesAsList.size(); j++) {
        FactorGraphNode node2 = nodesAsList.get(j);
        FactorGraphEdge edge = node1.connectTo(node2);
        this.edges.add(edge);
      }
    }
  }

  /**
   * Returns a moralized factor graph that represents the structure implicitly encoded in the factor scopes
   */
  public static FactorGraph fromGraphicalModel(Set<DiscreteFactor> factors) {
    Map<DiscreteVariable, FactorGraphNode> nodes = new HashMap<DiscreteVariable, FactorGraphNode>();
    
    for (DiscreteFactor factor : factors) {
      for (DiscreteVariable variable : factor.getVariables().getVariables()) {
        FactorGraphNode node = nodes.get(variable);
        
        if (node == null) {
          node = new FactorGraphNode(variable);
          nodes.put(variable, node);
        }
        
        node.addFactor(factor);
      }
    }
    
    FactorGraph factorGraph = new FactorGraph(nodes.values(), Collections.<FactorGraphEdge>emptySet());
    
    for (DiscreteFactor factor : factors) {
      Set<FactorGraphNode> nodesForFactor = new HashSet<FactorGraphNode>();
      
      for (DiscreteVariable variable : factor.getVariables().getVariables()) {
        nodesForFactor.add(nodes.get(variable));
      }
      
      factorGraph.connectPairwise(nodesForFactor);
    }
    
    return factorGraph;
  }
}
