package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;
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

  public double jointProbability(Scope projection, int[] variableAssignment) {
    return jointProbability(projection, variableAssignment, null, null);
  }
  
  public double jointProbability(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    return jointProbabilityDistribution(projection, observedVariables, observation).getValueForAssignment(variableAssignment);
  }
  
  public double jointProbabilityConditionedOn(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    DiscreteFactor jointMarginalDistribution = jointProbabilityDistribution(projection, observedVariables, observation);
    DiscreteFactor normalizedDistribution = jointMarginalDistribution.normalize();
    return normalizedDistribution.getValueForAssignment(variableAssignment);
  }
  
  // TODO: make public and test separately
  protected DiscreteFactor jointProbabilityDistribution(Scope projection, Scope observedVariables, int[] observation) {
    ensureJointDistributionInitialized();
    
    DiscreteFactor currentFactor = jointDistribution;
    if (observation != null && observedVariables != null) {
      currentFactor = currentFactor.observation(observedVariables, observation);
    }
    
    DiscreteFactor marginalDistribution = currentFactor.marginal(projection);
    return marginalDistribution;
  }
  
  protected void ensureJointDistributionInitialized() {
    if (jointDistribution == null) {
      jointDistribution = FactorUtil.jointDistribution(model.getFactors());
    }
  }

}
