package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Most simple inferencer that just builds the full joint probability distribution.
 * 
 * @author Thorben
 */
public class NaiveInferencer implements ExactInferencer {

  protected GraphicalModel model;
  protected DiscreteFactor jointDistribution;
  
  public NaiveInferencer(GraphicalModel model) {
    this.model = model;
  }

  public double jointProbability(int[] variableAssignment, Scope projection) {
    if (jointDistribution == null) {
      for (DiscreteFactor factor : model.getFactors()) {
        if (jointDistribution == null) {
          jointDistribution = factor;
        } else {
          jointDistribution = jointDistribution.product(factor);
        }
      }
    }
    
    DiscreteFactor marginalDistribution = jointDistribution.marginal(projection);
    return marginalDistribution.getValueForAssignment(variableAssignment);
  }
}
