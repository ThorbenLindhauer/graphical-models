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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;

public class FactorGraphNode {

  protected DiscreteVariable variable;
  protected Map<DiscreteVariable, FactorGraphEdge> edges;
  
  /** all the factors that the variable represented by this node is involved in */
  protected Set<DiscreteFactor> factors;

  public FactorGraphNode(DiscreteVariable variable) {
    this.variable = variable;
    this.edges = new HashMap<DiscreteVariable, FactorGraphEdge>();
    this.factors = new HashSet<DiscreteFactor>();
  }
  
  public void addFactor(DiscreteFactor factor) {
    this.factors.add(factor);
  }
  
  public Set<DiscreteFactor> getFactors() {
    return factors;
  }
  
  public DiscreteVariable getVariable() {
    return variable;
  }
  
  public Set<FactorGraphNode> getNeighbours() {
    Set<FactorGraphNode> neighbours = new HashSet<FactorGraphNode>();
    
    for (FactorGraphEdge edge : edges.values()) {
      neighbours.add(edge.getConnectedNode(this));
    }
    
    return neighbours;
  }
  
  public boolean isConnectedTo(FactorGraphNode other) {
    return getNeighbours().contains(other);
  }
  
  /**
   * Idempotent method. May be called multiple times with the same parameter, resulting in at most one edge
   */
  public FactorGraphEdge connectTo(FactorGraphNode other) {
    if (!edges.containsKey(other.variable)) {
      FactorGraphEdge edge = new FactorGraphEdge(this, other);
      
      this.edges.put(other.variable, edge);
      other.edges.put(this.variable, edge);
    }
    
    return edges.get(other.variable);
  }
  
  public Map<DiscreteVariable, FactorGraphEdge> getEdges() {
    return edges;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(variable.getId());
    sb.append("] => [");
    for (DiscreteVariable connectedVariable : edges.keySet()) {
      sb.append(connectedVariable.getId());
      sb.append(", ");
    }
    
    sb.append("]");
    
    return sb.toString();
  }
}
