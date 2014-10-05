package com.github.thorbenlindhauer.variable;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;


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
    return variables.values();
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
}
