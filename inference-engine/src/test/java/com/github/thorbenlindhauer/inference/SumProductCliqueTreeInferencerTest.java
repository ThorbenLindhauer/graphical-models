package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.cluster.messagepassing.SumProductContextFactory;

public class SumProductCliqueTreeInferencerTest extends AbstractCliqueTreeInferencerTest {

  @Override
  protected MessagePassingContextFactory getMessagePassingContextFactory() {
    return new SumProductContextFactory();
  }

}
