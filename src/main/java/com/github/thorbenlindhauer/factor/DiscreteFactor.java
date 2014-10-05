package com.github.thorbenlindhauer.factor;

import com.github.thorbenlindhauer.variable.Variables;


public interface DiscreteFactor {

  DiscreteFactor product(DiscreteFactor other);
  
  DiscreteFactor marginal(Variables variables);
  
  Variables getVariables();
  
  double getValueForAssignment(int[] assignment);
  
  
}
