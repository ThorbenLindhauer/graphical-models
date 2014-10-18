package com.github.thorbenlindhauer.variable;

import com.github.thorbenlindhauer.exception.ModelStructureException;

public class IndexCoder {

  protected int[] variableCardinalities;
  protected int[] strides;
  protected int maxIndex;
  
  public IndexCoder(int[] variableCardinalities) {
    this.variableCardinalities = variableCardinalities;
    this.strides = new int[variableCardinalities.length];
    
    maxIndex = 1;
    
    for (int i = 0; i < variableCardinalities.length; i++) {
      strides[i] = maxIndex;
      maxIndex *= variableCardinalities[i];
    }
  }
  
  public int getIndexForAssignment(int[] variableAssignment) {
    // TODO: vector operations would be useful here
    
    int index = 0;
    int multiple = 1;
    for (int i = 0; i < variableCardinalities.length; i++) {
      index += multiple * variableAssignment[i];
      multiple *= variableCardinalities[i];
    }
    
    return index;
  }
  
  public int[] getAssignmentForIndex(int index) {
    int[] assignment = new int[variableCardinalities.length];
    int multiple = maxIndex;
    
    for (int i = variableCardinalities.length - 1; i >= 0; i--) {
      multiple /= variableCardinalities[i];
      assignment[i] = index / multiple;
      index -= assignment[i] * multiple;
    }
    
    return assignment;
  }
  
  /**
   * Returns the assignment value for the given index and position. Position here means
   * the index in the assignment array. Thus, the return value is a single integer.
   */
  public int getAssignmentAtPositionForIndex(int index, int position) {
    return (index / strides[position]) % variableCardinalities[position];
  }
  
  public IndexMapper getIndexMapper(int[] mapping) {
    if (mapping.length != variableCardinalities.length) {
      throw new ModelStructureException("Invalid mapping " + mapping + " for cardinalities " + variableCardinalities);
    }
    
    int[] mappedVariableCardinalities = new int[variableCardinalities.length];
    for (int i = 0; i < mappedVariableCardinalities.length; i++) {
      mappedVariableCardinalities[mapping[i]] = variableCardinalities[i];
    }
    
    return new IndexMapper(this, new IndexCoder(mappedVariableCardinalities), mapping);
  }
  
  public int[] getCardinalities() {
    return variableCardinalities;
  }
  
  public int[] getStrides() {
    return strides;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    sb.append("Cardinalities: ");
    sb.append(variableCardinalities);
    
    return sb.toString();
  }
}
