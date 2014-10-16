package com.github.thorbenlindhauer.variable;

import java.util.BitSet;

import com.github.thorbenlindhauer.exception.ModelStructureException;

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
  
  // TODO: refactor such that a projection returns a new coder that is able to cache certain things?
  public int[] getIndexesForProjectedAssignment(int[] projectedAssignment, BitSet projection) {
    int unprojectedCardinality = 1;
    
    for (int i = 0; i < variableCardinalities.length; i++) {
      if (!projection.get(i)) {
        unprojectedCardinality *= variableCardinalities[i];
      }
    }
    
    int[] indexes = new int[unprojectedCardinality];
    
    BitSet indexIndicator = new BitSet(maxIndex);
    indexIndicator.flip(0, maxIndex);
    
    int varIndex = 0;
    for (int i = 0; i < projectedAssignment.length; i++) {
      varIndex = projection.nextSetBit(varIndex);
      
      int lowerCardinalities = 1;
      int higherCardinalities = 1;
      
      for (int j = 0; j < variableCardinalities.length; j++) {
        if (j < varIndex) {
          lowerCardinalities *= variableCardinalities[j];
          
        } else if (j > varIndex) {
          higherCardinalities *= variableCardinalities[j];
        }
      }
      
      int offset = lowerCardinalities * projectedAssignment[i];
      
      // number of blocks of subsequent assignments that have the projected value
      int numAssignmentBlocks = higherCardinalities;
      
      BitSet validAssignmentsForVariable = new BitSet(maxIndex);
      for (int j = 0; j < numAssignmentBlocks; j++) {
        for (int k = 0; k < lowerCardinalities; k++) {
          validAssignmentsForVariable.set((j * lowerCardinalities * variableCardinalities[varIndex]) + offset + k);
        }
      }
      
      indexIndicator.and(validAssignmentsForVariable);
      varIndex++;
    }
    
    int addedIndexes = 0;
    int nextIndex = indexIndicator.nextSetBit(0);
    while (nextIndex != -1) {
      indexes[addedIndexes++] = nextIndex;
      nextIndex = indexIndicator.nextSetBit(++nextIndex);
    }
    
    return indexes;
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
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    sb.append("Cardinalities: ");
    sb.append(variableCardinalities);
    
    return sb.toString();
  }
}
