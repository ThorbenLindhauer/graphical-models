package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.inference.variableelimination.RandomEliminationStrategy;

public class VariableEliminationRandomInferencerTest extends ExactInferencerTest {

  @Override
  protected ExactInferencer getInferencer() {
    return new VariableEliminationInferencer(model, new RandomEliminationStrategy());
  }

}
