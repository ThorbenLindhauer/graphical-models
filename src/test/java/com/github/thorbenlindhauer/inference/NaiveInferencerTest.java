package com.github.thorbenlindhauer.inference;

public class NaiveInferencerTest extends ExactInferencerTest {

  @Override
  protected ExactInferencer getInferencer() {
    return new NaiveInferencer(model);
  }

}
