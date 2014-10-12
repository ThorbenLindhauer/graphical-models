package com.github.thorbenlindhauer.network;

import java.util.HashMap;
import java.util.Map;

import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class ScopeBuilderImpl implements ScopeBuilder {
  
  protected Map<String, DiscreteVariable> variables;
  
  public ScopeBuilderImpl() {
    this.variables = new HashMap<String, DiscreteVariable>();
  }

  public ScopeBuilder variable(String id, int cardinality) {
    DiscreteVariable variable = new DiscreteVariable(id, cardinality);
    variables.put(id, variable);
    return this;
  }
  
  public ModelBuilder done() {
    Scope scope = new Scope(variables.values());
    return new ModelBuilderImpl(scope);
  }
  
  
}
