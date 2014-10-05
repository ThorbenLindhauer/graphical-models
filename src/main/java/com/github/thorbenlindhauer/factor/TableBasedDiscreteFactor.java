package com.github.thorbenlindhauer.factor;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.IndexCoder;
import com.github.thorbenlindhauer.variable.Variables;

/**
 * Factors are immutable.
 * 
 * @author Thorben
 */
public class TableBasedDiscreteFactor implements DiscreteFactor {

  protected Variables variables;
  protected double[] values;
  
  public TableBasedDiscreteFactor(Variables variables, double[] values) {
    if (variables == null || variables.isEmpty()) {
      throw new ModelStructureException("Factor must be defined over at least one variable");
    }
    
    this.variables = variables;
    this.values = values;
  }
  
  
  // TODO: think about making this a varargs method
  public TableBasedDiscreteFactor product(DiscreteFactor other) {
    Set<DiscreteVariable> newVars = new HashSet<DiscreteVariable>();
    newVars.addAll(this.variables.getVariables());
    newVars.addAll(other.getVariables().getVariables());
    
    Variables newVariables = new Variables(newVars);
    
    BitSet varsFactor1 = newVariables.getProjection(variables);
    BitSet varsFactor2 = newVariables.getProjection(other.getVariables());
    
    double[] newValues = new double[newVariables.getNumDistinctValues()];
    IndexCoder indexCoder = newVariables.getIndexCoder();
    
    for (int j = 0; j < newValues.length; j++) {
      int[] assignment = indexCoder.getAssignmentForIndex(j);
      int[] assignmentFactor1 = IndexCoder.projectAssignment(assignment, varsFactor1);
      int[] assignmentFactor2 = IndexCoder.projectAssignment(assignment, varsFactor2);
      
      double valueFactor1 = this.getValueForAssignment(assignmentFactor1);
      double valueFactor2 = other.getValueForAssignment(assignmentFactor2);
      newValues[j] = valueFactor1 * valueFactor2;
    }
    
    TableBasedDiscreteFactor newFactor = new TableBasedDiscreteFactor(newVariables, newValues);
    return newFactor;
  }
  
  public Variables getVariables() {
    return variables;
  }


  public double[] getValues() {
    return values;
  }


  public double getValueForAssignment(int[] assignment) {
    int index = variables.getIndexCoder().getIndexForAssignment(assignment);
    return values[index];
  }
}
