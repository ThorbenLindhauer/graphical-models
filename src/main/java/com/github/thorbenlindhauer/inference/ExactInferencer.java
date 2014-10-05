package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.variable.Scope;

public interface ExactInferencer {

  // TODO: add methods for getting the overall probability distribution, adding observations, etc.
  
  double jointProbability(int[] variableAssignment, Scope projection);
}
