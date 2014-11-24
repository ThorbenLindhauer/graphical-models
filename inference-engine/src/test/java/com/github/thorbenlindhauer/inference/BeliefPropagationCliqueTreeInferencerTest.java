package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.cluster.messagepassing.BeliefUpdateContextFactory;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;

public class BeliefPropagationCliqueTreeInferencerTest extends AbstractCliqueTreeInferencerTest {

  @Override
  protected MessagePassingContextFactory getMessagePassingContextFactory() {
    return new BeliefUpdateContextFactory();
  }

}
