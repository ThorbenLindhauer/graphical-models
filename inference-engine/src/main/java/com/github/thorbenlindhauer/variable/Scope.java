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
package com.github.thorbenlindhauer.variable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.thorbenlindhauer.exception.ModelStructureException;


//TODO: refactor to common interface and subclass DiscreteVariables ?
//TODO: implement Map interface?
public class Scope {

  // inconsistencies in these maps should not arise since a scope is immutable
  protected Map<String, Variable> variables;
  protected Map<String, DiscreteVariable> discreteVariables;
  protected Map<String, ContinuousVariable> continuousVariables;

  protected String[] sortedVariableIds;
  protected String[] discreteSortedVariableIds;
  protected IndexCoder indexCoder;
  protected int distinctValues;

  protected String[] continuousSortedVariableIds;

  public Scope(Collection<? extends Variable> variables) {
    this.variables = new HashMap<String, Variable>();
    this.discreteVariables = new HashMap<String, DiscreteVariable>();
    this.continuousVariables = new HashMap<String, ContinuousVariable>();

    for (Variable variable : variables) {
      this.variables.put(variable.getId(), variable);

      if (DiscreteVariable.class.isAssignableFrom(variable.getClass())) {
        this.discreteVariables.put(variable.getId(), (DiscreteVariable) variable);
      }

      if (ContinuousVariable.class.isAssignableFrom(variable.getClass())) {
        this.continuousVariables.put(variable.getId(), (ContinuousVariable) variable);
      }
    }

    sortedVariableIds = new TreeSet<String>(this.variables.keySet()).toArray(new String[]{});
    continuousSortedVariableIds = new TreeSet<String>(this.continuousVariables.keySet()).toArray(new String[]{});
    discreteSortedVariableIds = new TreeSet<String>(this.discreteVariables.keySet()).toArray(new String[]{});

    int[] cardinalities = new int[this.variables.size()];

    distinctValues = 1;
    for (int i = 0; i < discreteSortedVariableIds.length; i++) {
      DiscreteVariable variable = this.discreteVariables.get(discreteSortedVariableIds[i]);
      cardinalities[i] = variable.getCardinality();
      distinctValues *= variable.getCardinality();
    }

    indexCoder = new IndexCoder(cardinalities);
  }

  /**
   * Returns an array of the length that this scope has variables.
   * Each array entry is an index into the other scope's variables or -1 if the other scope
   * does not have this variable.
   *
   * Maps only discrete variables
   */
  public int[] createDiscreteVariableMapping(Scope other) {
    int[] mapping = new int[discreteSortedVariableIds.length];

    for (int i = 0; i < discreteSortedVariableIds.length; i++) {
      String variable = discreteSortedVariableIds[i];
      mapping[i] = -1;
      if (other.has(variable)) {
        // TODO: improve this by caching a mapping of variable id to position
        for (int j = 0; j < other.discreteSortedVariableIds.length; j++) {
          String otherVariable = other.discreteSortedVariableIds[j];
          if (variable.equals(otherVariable)) {
            mapping[i] = j;
          }
        }
      }
    }

    return mapping;
  }

  public int[] createContinuousVariableMapping(Scope other) {
    int[] mapping = new int[continuousSortedVariableIds.length];

    for (int i = 0; i < continuousSortedVariableIds.length; i++) {
      String variable = continuousSortedVariableIds[i];
      mapping[i] = -1;
      if (other.has(variable)) {
        // TODO: improve this by caching a mapping of variable id to position
        for (int j = 0; j < other.continuousSortedVariableIds.length; j++) {
          String otherVariable = other.continuousSortedVariableIds[j];
          if (variable.equals(otherVariable)) {
            mapping[i] = j;
          }
        }
      }
    }

    return mapping;
  }

  public Collection<Variable> getVariables() {
    return new HashSet<Variable>(variables.values());
  }

  public Collection<DiscreteVariable> getDiscreteVariables() {
    return new HashSet<DiscreteVariable>(discreteVariables.values());
  }

  public Collection<ContinuousVariable> getContinuousVariables() {
    return new HashSet<ContinuousVariable>(continuousVariables.values());
  }

  public String[] getVariableIds() {
    return sortedVariableIds;
  }

  public boolean hasSameVariablesAs(Scope other) {
    return variables.keySet().equals(other.variables.keySet());
  }

  public boolean has(Variable variable) {
    return this.variables.containsKey(variable.getId());
  }

  public boolean contains(Scope other) {
    return this.variables.keySet().containsAll(other.variables.keySet());
  }

  public boolean contains(String... variableIds) {
    Set<String> variableIdSet = variables.keySet();
    for (String variableId : variableIds) {
      if (!variableIdSet.contains(variableId)) {
        return false;
      }
    }

    return true;
  }

  public boolean has(String variableId) {
    return this.variables.containsKey(variableId);
  }

  public Variable getVariable(String variableId) {
    return variables.get(variableId);
  }

  public String getVariableId(int index) {
    return sortedVariableIds[index];
  }

  public IndexCoder getIndexCoder() {
    return indexCoder;
  }

  public int getNumDistinctValues() {
    return distinctValues;
  }

  public boolean isEmpty() {
    return variables.isEmpty();
  }

  public Scope subScope(String... variableIds) {
    Set<Variable> subVariables = new HashSet<Variable>();

    for (String variableId : variableIds) {
      if (!has(variableId)) {
        throw new ModelStructureException("Variable " + variableId + " is not part of this scope.");
      }

      subVariables.add(variables.get(variableId));
    }

    return new Scope(subVariables);
  }

  public Scope intersect(Scope other) {
    Set<Variable> retainedVariables = new HashSet<Variable>();

    for (Variable variable : variables.values()) {
      if (other.has(variable)) {
        retainedVariables.add(variable);
      }
    }

    return new Scope(retainedVariables);
  }

  public Scope union(Scope other) {
    Set<Variable> newVariables = new HashSet<Variable>(variables.values());
    newVariables.addAll(other.variables.values());

    return new Scope(newVariables);
  }

  /**
   * Returns a new, reduced scope.
   */
  public Scope reduceBy(String... variableIds) {
    Map<String, Variable> newVariables = new HashMap<String, Variable>(variables);

    for (String variableId : variableIds) {
      newVariables.remove(variableId);
    }

    return new Scope(newVariables.values());
  }

  /**
   * Returns a new, reduced scope.
   */
  public Scope reduceBy(Scope other) {
    return reduceBy(other.sortedVariableIds);
  }

  public int size() {
    return sortedVariableIds.length;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("[");

    int i = 0;
    for (String variableId : variables.keySet()) {
      sb.append(variableId);

      if (i != variables.size() - 1) {
        sb.append(", ");
      }

      i++;
    }

    sb.append("]");

    return sb.toString();
  }
}
