package com.github.thorbenlindhauer.graph.operation;

import com.github.thorbenlindhauer.factorgraph.FactorGraph;

public interface FactorGraphOperation<T> {

  public T execute(FactorGraph factorGraph);
}
