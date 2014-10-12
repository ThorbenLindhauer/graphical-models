package com.github.thorbenlindhauer.inference.variableelimination;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.thorbenlindhauer.network.GraphicalModel;

public class RandomEliminationStrategy implements VariableEliminationStrategy {

  public List<String> getEliminationOrder(GraphicalModel graphicalModel, Collection<String> variablesToEliminate) {
    return Arrays.asList(variablesToEliminate.toArray(new String[]{}));
  }

}
