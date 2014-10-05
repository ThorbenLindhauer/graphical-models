package com.github.thorbenlindhauer.factor;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.IndexCoder;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Factors are immutable.
 * 
 * @author Thorben
 */
public class TableBasedDiscreteFactor implements DiscreteFactor {

  protected Scope variables;
  protected double[] values;
  
  public TableBasedDiscreteFactor(Scope variables, double[] values) {
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
    
    Scope newVariables = new Scope(newVars);
    
    BitSet varsFactor1 = newVariables.getProjection(variables);
    BitSet varsFactor2 = newVariables.getProjection(other.getVariables());
    
    double[] newValues = new double[newVariables.getNumDistinctValues()];
    IndexCoder indexCoder = newVariables.getIndexCoder();
    
    for (int i = 0; i < newValues.length; i++) {
      int[] assignment = indexCoder.getAssignmentForIndex(i);
      int[] assignmentFactor1 = IndexCoder.projectAssignment(assignment, varsFactor1);
      int[] assignmentFactor2 = IndexCoder.projectAssignment(assignment, varsFactor2);
      
      double valueFactor1 = this.getValueForAssignment(assignmentFactor1);
      double valueFactor2 = other.getValueForAssignment(assignmentFactor2);
      newValues[i] = valueFactor1 * valueFactor2;
    }
    
    TableBasedDiscreteFactor newFactor = new TableBasedDiscreteFactor(newVariables, newValues);
    return newFactor;
  }

  public TableBasedDiscreteFactor marginal(Scope variables) {
    if (!this.variables.getVariables().containsAll(variables.getVariables())) {
      throw new ModelStructureException("argument variables are not all contained by this factor");
    }
    
    double[] newValues = new double[variables.getNumDistinctValues()];
    IndexCoder indexCoder = variables.getIndexCoder();
    
    for (int i = 0; i < newValues.length; i++) {
      int[] assignment = indexCoder.getAssignmentForIndex(i);
      BitSet projection = this.variables.getProjection(variables);
      newValues[i] = sumValuesForAssignment(assignment, projection);
    }
    
    return new TableBasedDiscreteFactor(variables, newValues);
  }

  // TODO: consider implementing this as a view on the original factor
  public TableBasedDiscreteFactor observation(Scope scope, int[] observedValues) {
    if (scope.getVariables().size() != observedValues.length) {
      // TODO: add cardinality check
      throw new ModelStructureException("Observed variables and values do not match");
    }
    
    double[] newValues = new double[values.length];
    BitSet projection = variables.getProjection(scope);
    int[] indexesToRetain = variables.getIndexCoder().getIndexesForProjectedAssignment(observedValues, projection);
    
    for (int indexToRetain : indexesToRetain) {
      newValues[indexToRetain] = values[indexToRetain];
    }
    
    TableBasedDiscreteFactor newFactor = new TableBasedDiscreteFactor(variables, newValues);
    
    return newFactor;
  }
  
  public Scope getVariables() {
    return variables;
  }


  public double[] getValues() {
    return values;
  }


  public double getValueForAssignment(int[] assignment) {
    int index = variables.getIndexCoder().getIndexForAssignment(assignment);
    return values[index];
  }
  
  public double sumValuesForAssignment(int[] assignment, BitSet projection) {
    int[] indexes = variables.getIndexCoder().getIndexesForProjectedAssignment(assignment, projection);
    
    double sum = 0.0d;
    for (int index : indexes) {
      sum += values[index];
    }
    
    return sum;
  }


  public TableBasedDiscreteFactor normalize() {
    double valueSum = 0.0d;
    
    for (double value : values) {
      valueSum += value;
    }
    
    double[] newValues = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      if (values[i] != 0) {
        newValues[i] = values[i] / valueSum;
      }
    }
    
    return new TableBasedDiscreteFactor(variables, newValues);
  }



}
