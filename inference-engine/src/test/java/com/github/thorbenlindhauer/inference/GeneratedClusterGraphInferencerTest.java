package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.cluster.generation.CliqueTreeGenerator;
import com.github.thorbenlindhauer.cluster.messagepassing.BeliefPropagationContextFactory;

public class GeneratedClusterGraphInferencerTest extends ExactInferencerTest {

  @Override
  protected ExactInferencer getInferencer() {
    return new GeneratedCliqueTreeInferencer(model, new CliqueTreeGenerator(), new BeliefPropagationContextFactory());
  }
}
