package com.github.thorbenlindhauer.network;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class FactorBuilderImpl implements FactorBuilder {

  protected ModelBuilderImpl modelBuilder;
  protected Scope graphScope;
  protected Set<DiscreteVariable> factorVariables;
  
  public FactorBuilderImpl(ModelBuilderImpl modelBuilder, Scope scope) {
    this.modelBuilder = modelBuilder;
    this.graphScope = scope;
    this.factorVariables = new HashSet<DiscreteVariable>();
  }
  
  public FactorBuilder scope(String... variableIds) {
    for (String variableId : variableIds) {
      DiscreteVariable variable = graphScope.getVariable(variableId);
      
      if (variable == null) {
        throw new ModelStructureException("Variable " + variableId + " not defined in scope of graph.");
      }
      
      factorVariables.add(variable);
    }
    
    return this;
  }
  
  public ModelBuilder basedOnTable(double[] table) {
    Scope factorScope = new Scope(factorVariables);
    DiscreteFactor factor = new TableBasedDiscreteFactor(factorScope, table);
    modelBuilder.addFactor(factor);
    
    return modelBuilder;
  }
  
}
