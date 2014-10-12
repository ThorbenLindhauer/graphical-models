package com.github.thorbenlindhauer.variable;

/**
 * Maps indexes of variable assignments according to a provided mapping. Useful when input is not specified in the
 * canonical ordering.
 * 
 * @author Thorben
 */
public class IndexMapper {

  protected IndexCoder originalCoder;
  protected IndexCoder mappedCoder;
  protected int[] mapping;
  
  public IndexMapper(IndexCoder originalCoder, IndexCoder mappedCoder, int[] mapping) {
    this.originalCoder = originalCoder;
    this.mappedCoder = mappedCoder;
    this.mapping = mapping;
  }
  
  public int mapIndex(int originalIndex) {
    int[] originalAssignment = originalCoder.getAssignmentForIndex(originalIndex);
    int[] mappedAssignment = map(originalAssignment);
    return mappedCoder.getIndexForAssignment(mappedAssignment);
  }
  
  public int reverseMapIndex(int mappedIndex) {
    int[] mappedAssignment = mappedCoder.getAssignmentForIndex(mappedIndex);
    int[] originalAssignment = reverseMap(mappedAssignment);
    return originalCoder.getIndexForAssignment(originalAssignment);
  }
  
  protected int[] map(int[] assignment) {
    int[] mappedAssignment = new int[assignment.length];
    
    for (int i = 0; i < mapping.length; i++) {
      mappedAssignment[mapping[i]] = assignment[i];
    }
    
    return mappedAssignment;
  }
  
  protected int[] reverseMap(int[] assignment) {
    int[] mappedAssignment = new int[assignment.length];
    
    for (int i = 0; i < mapping.length; i++) {
      mappedAssignment[i] = assignment[mapping[i]];
    }
    
    return mappedAssignment;
  }
}
