package com.github.thorbenlindhauer.factor;

import com.github.thorbenlindhauer.variable.Scope;


public interface DiscreteFactor {

  DiscreteFactor product(DiscreteFactor other);
  
  DiscreteFactor marginal(Scope variables);
  
  Scope getVariables();
  
  double getValueForAssignment(int[] assignment);
  
  
}
