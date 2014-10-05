package com.github.thorbenlindhauer.variable;

import java.util.BitSet;

public class IndexCoder {

  protected int[] variableCardinalities;
  protected int maxIndex;
  
  public IndexCoder(int[] variableCardinalities) {
    this.variableCardinalities = variableCardinalities;
    
    maxIndex = 1;
    
    for (int cardinality : variableCardinalities) {
      maxIndex *= cardinality;
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
  
  public int[] getAssignmentForIndex(int index, BitSet projection) {
    int[] assignment = getAssignmentForIndex(index);
    return projectAssignment(assignment, projection);
  }
  
  public static int[] projectAssignment(int[] assignment, BitSet projection) {
    int[] projectedAssignment = new int[projection.cardinality()];
    
    int newAssignmentIndex = 0;
    for (int i = 0; i < projection.length(); i++) {
      if (projection.get(i)) {
        projectedAssignment[newAssignmentIndex] = assignment[i];
        newAssignmentIndex++;
      }
    }
    
    return projectedAssignment;
  }
}
