package com.github.thorbenlindhauer.variable;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.thorbenlindhauer.exception.ModelStructureException;


//TODO: refactor to common interface and subclass DiscreteVariables ?
//TODO: implement Map interface?
public class Scope {

  protected SortedMap<String, DiscreteVariable> variables;
  protected IndexCoder indexCoder;
  protected int distinctValues;
  
  public Scope(Collection<DiscreteVariable> variables) {
    this.variables = new TreeMap<String, DiscreteVariable>();
    
    for (DiscreteVariable variable : variables) {
      this.variables.put(variable.getId(), variable);
    }
    
    int[] cardinalities = new int[this.variables.size()];
    
    int cardId = 0;
    distinctValues = 1;
    for (DiscreteVariable variable : this.variables.values()) {
      cardinalities[cardId++] = variable.getCardinality();
      distinctValues *= variable.getCardinality();
    }
    
    indexCoder = new IndexCoder(cardinalities);
  }
  
  
  public void addAll(Collection<DiscreteVariable> variablesToAdd) {
    for (DiscreteVariable variableToAdd : variablesToAdd) {
      variables.put(variableToAdd.getId(), variableToAdd);
    }
  }
  
  public Iterator<DiscreteVariable> canonicalIterator() {
    return variables.values().iterator();
  }
  
  /**
   * Returns a BitSet that has a 1 at every place that corresponds to a variable index that is part of the argument.
   * @param variablesToBeProjected
   * @return
   */
  public BitSet getProjection(Scope variablesToBeProjected) {
    BitSet projection = new BitSet();
    int currentBit = 0;
    Iterator<DiscreteVariable> canonicalIt = canonicalIterator();
    while (canonicalIt.hasNext()) {
      DiscreteVariable nextVar = canonicalIt.next();
      
      if (variablesToBeProjected.has(nextVar)) {
        projection.set(currentBit);
      }
      
      currentBit++;
    }
    
    return projection;
  }
  
  public Collection<DiscreteVariable> getVariables() {
    return new HashSet<DiscreteVariable>(variables.values());
  }
  
  public Collection<String> getVariableIds() {
    return new HashSet<String>(variables.keySet());
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
  
  public Scope removeAll(String... variableIds) {
    Map<String, DiscreteVariable> newVariables = new HashMap<String, DiscreteVariable>(variables);
   
    for (String variableId : variableIds) {
      newVariables.remove(variableId);
    }
    
    return new Scope(newVariables.values());
  }
  
  public Scope removeAll(Scope other) {
    Collection<String> variables = other.getVariableIds();
    
    return removeAll(variables.toArray(new String[variables.size()]));
  }
}
