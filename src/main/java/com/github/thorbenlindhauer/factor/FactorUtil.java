package com.github.thorbenlindhauer.factor;

import java.util.Set;

public class FactorUtil {

  public static DiscreteFactor jointDistribution(Set<DiscreteFactor> factors) {
    DiscreteFactor jointDistribution = null;
    
    for (DiscreteFactor factor : factors) {
      if (jointDistribution == null) {
        jointDistribution = factor;
      } else {
        jointDistribution = jointDistribution.product(factor);
      }
    }
    
    return jointDistribution;
  }
}
