package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.cluster.messagepassing.BeliefPropagationContextFactory;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;

public class BeliefPropagationCliqueTreeInferencerTest extends AbstractCliqueTreeInferencerTest {

  @Override
  protected MessagePassingContextFactory getMessagePassingContextFactory() {
    return new BeliefPropagationContextFactory();
  }

}
