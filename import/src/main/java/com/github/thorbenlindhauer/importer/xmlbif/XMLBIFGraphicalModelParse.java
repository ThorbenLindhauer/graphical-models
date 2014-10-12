package com.github.thorbenlindhauer.importer.xmlbif;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class XMLBIFGraphicalModelParse {

  protected Map<String, DiscreteVariable> scope;
  protected Set<DiscreteFactor> factors;
  
  public XMLBIFGraphicalModelParse() {
    this.scope = new HashMap<String, DiscreteVariable>();
    this.factors = new HashSet<DiscreteFactor>();
  }
  
  public void addVariable(DiscreteVariable variable) {
    this.scope.put(variable.getId(), variable);
  }
  
  public void addFactor(DiscreteFactor factor) {
    this.factors.add(factor);
  }
  
  public DiscreteVariable getVariable(String id) {
    return scope.get(id);
  }
  
  public Collection<DiscreteVariable> getVariables() {
    return scope.values();
  }
  
  public Set<DiscreteFactor> getFactors() {
    return factors;
  }
  
  public Scope scopeFor(String... variableIds) {
    Set<DiscreteVariable> variables = new HashSet<DiscreteVariable>();
    
    for (String variableId : variableIds) {
      variables.add(scope.get(variableId));
    }
    
    return new Scope(variables);
  }
}
