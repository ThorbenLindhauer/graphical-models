package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.inference.variableelimination.MinFillEliminationStrategy;

public class VariableEliminationMinFillInferencerTest extends ExactInferencerTest {

  @Override
  protected ExactInferencer getInferencer() {
    return new VariableEliminationInferencer(model, new MinFillEliminationStrategy());
  }

}
