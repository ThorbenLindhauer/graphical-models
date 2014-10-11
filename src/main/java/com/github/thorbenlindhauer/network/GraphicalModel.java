package com.github.thorbenlindhauer.network;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

// TODO: essentially, this could also be a factor
public class GraphicalModel {

  protected Set<DiscreteFactor> factors;
  protected Scope scope;
  
  public GraphicalModel(Scope scope, Set<DiscreteFactor> factors) {
    this.factors = factors;
    this.scope = scope;
  }

  public Set<DiscreteFactor> getFactors() {
    return new HashSet<DiscreteFactor>(factors);
  }

  public Scope getScope() {
    return scope;
  }
  
  public static ScopeBuilder create() {
    return new ScopeBuilderImpl();
  }
}
