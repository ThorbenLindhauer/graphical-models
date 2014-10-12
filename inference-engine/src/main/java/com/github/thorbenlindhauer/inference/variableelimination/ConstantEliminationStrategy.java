package com.github.thorbenlindhauer.inference.variableelimination;

import java.util.Collection;
import java.util.List;

import com.github.thorbenlindhauer.network.GraphicalModel;

public class ConstantEliminationStrategy implements VariableEliminationStrategy {

  protected List<String> eliminationOrder;
  
  public ConstantEliminationStrategy(List<String> eliminationOrder) {
    this.eliminationOrder = eliminationOrder;
  }

  public List<String> getEliminationOrder(GraphicalModel graphicalModel, Collection<String> variablesToEliminate) {
    return eliminationOrder;
  }
}
