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

  protected Map<String, DiscreteVariable> variables;
  protected String[] sortedVariableIds;
  protected IndexCoder indexCoder;
  protected int distinctValues;
  
  public Scope(Collection<DiscreteVariable> variables) {
    this.variables = new HashMap<String, DiscreteVariable>();
    
    for (DiscreteVariable variable : variables) {
      this.variables.put(variable.getId(), variable);
    }
    
    sortedVariableIds = new TreeSet<String>(this.variables.keySet()).toArray(new String[]{});
    
    int[] cardinalities = new int[this.variables.size()];
    
    distinctValues = 1;
    for (int i = 0; i < sortedVariableIds.length; i++) {
      DiscreteVariable variable = this.variables.get(sortedVariableIds[i]);
      cardinalities[i] = variable.getCardinality();
      distinctValues *= variable.getCardinality();
    }
    
    indexCoder = new IndexCoder(cardinalities);
  }
  
  /**
   * Returns an array of the length that this scope has variables.
   * Each array entry is an index into the other scope's variables or -1 if the other scope
   * does not have this variable.
   */
  public int[] createMapping(Scope other) {
    int[] mapping = new int[sortedVariableIds.length];
    
    for (int i = 0; i < sortedVariableIds.length; i++) {
      String variable = sortedVariableIds[i];
      mapping[i] = -1;
      if (other.has(variable)) {
        // TODO: improve this by caching a mapping of variable id to position
        for (int j = 0; j < other.sortedVariableIds.length; j++) {
          String otherVariable = other.sortedVariableIds[j];
          if (variable.equals(otherVariable)) {
            mapping[i] = j;
          }
        }
      }
    }
    
    return mapping;
  }
  
  public Collection<DiscreteVariable> getVariables() {
    return new HashSet<DiscreteVariable>(variables.values());
  }
  
  public String[] getVariableIds() {
    return sortedVariableIds;
  }
  
  public boolean hasSameVariablesAs(Scope other) {
    return variables.keySet().equals(other.variables.keySet());
  }
  
  public boolean has(DiscreteVariable variable) {
    return this.variables.containsKey(variable.getId());
  }
  
  public boolean has(String variableId) {
    return this.variables.containsKey(variableId);
  }
  
  public DiscreteVariable get(String variableId) {
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
    Set<DiscreteVariable> subVariables = new HashSet<DiscreteVariable>();
    
    for (String variableId : variableIds) {
      if (!has(variableId)) {
        throw new ModelStructureException("Variable " + variableId + " is not part of this scope.");
      }
      
      subVariables.add(variables.get(variableId));
    }
    
    return new Scope(subVariables);
  }
  
  public Scope intersect(Scope other) {
    Set<DiscreteVariable> retainedVariables = new HashSet<DiscreteVariable>();
    
    for (DiscreteVariable variable : variables.values()) {
      if (other.has(variable)) {
        retainedVariables.add(variable);
      }
    }
    
    return new Scope(retainedVariables);
    
  }
  
  /**
   * Returns a new, reduced scope.
   */
  public Scope reduceBy(String... variableIds) {
    Map<String, DiscreteVariable> newVariables = new HashMap<String, DiscreteVariable>(variables);
   
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
