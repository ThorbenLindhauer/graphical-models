package com.github.thorbenlindhauer.factor;

import com.github.thorbenlindhauer.variable.Scope;


public interface DiscreteFactor {

  DiscreteFactor product(DiscreteFactor other);
  
  DiscreteFactor division(DiscreteFactor other);
  
  /**
   * @param scope variables to keep
   * @return
   */
  DiscreteFactor marginal(Scope scope);
  
  DiscreteFactor observation(Scope scope, int[] values);
  
  DiscreteFactor normalize();
  
  Scope getVariables();
  
  double getValueForAssignment(int[] assignment);
  
  double getValueAtIndex(int index);
  
  
}
