package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.variable.Scope;

public interface ExactInferencer {

  // TODO: add methods for getting the overall probability distribution, adding observations, etc.
  
  /**
   * P(Y)
   */
  double jointProbability(Scope projection, int[] variableAssignment);
  
  /**
   * P(Y, E = e)
   */
  public double jointProbability(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation);
  
  /**
   * P(Y | E = e)
   */
  public double jointProbabilityConditionedOn(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation);
}
