package com.github.thorbenlindhauer.network;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

public class ModelBuilderImpl implements ModelBuilder {

  protected Set<DiscreteFactor> factors;
  protected Scope scope;
  
  public ModelBuilderImpl(Scope scope) {
    this.factors = new HashSet<DiscreteFactor>();
    this.scope = scope;
  }
  
  public FactorBuilder factor() {
    return new FactorBuilderImpl(this, scope);
  }
  
  public void addFactor(DiscreteFactor factor) {
    factors.add(factor);
  }
  
  public GraphicalModel build() {
    // TODO: validate model here
    GraphicalModel model = new GraphicalModel(scope, factors);
    return model;
  }
}
