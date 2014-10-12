package com.github.thorbenlindhauer.inference.variableelimination;

import java.util.Collection;
import java.util.List;

import com.github.thorbenlindhauer.network.GraphicalModel;

public interface VariableEliminationStrategy {

  List<String> getEliminationOrder(GraphicalModel graphicalModel, Collection<String> variablesToEliminate);
}
