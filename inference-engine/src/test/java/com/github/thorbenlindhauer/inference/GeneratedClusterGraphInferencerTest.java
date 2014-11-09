package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.cluster.messagepassing.BeliefPropagationContextFactory;
import com.github.thorbenlindhauer.graph.operation.ClusterGraphGenerator;

public class GeneratedClusterGraphInferencerTest extends ExactInferencerTest {

  @Override
  protected ExactInferencer getInferencer() {
    return new GeneratedCliqueTreeInferencer(model, new ClusterGraphGenerator(), new BeliefPropagationContextFactory());
  }
}
