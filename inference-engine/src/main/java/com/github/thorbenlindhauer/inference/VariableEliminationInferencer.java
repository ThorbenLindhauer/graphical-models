package com.github.thorbenlindhauer.inference;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.thorbenlindhauer.exception.InferenceException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.inference.variableelimination.VariableEliminationStrategy;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Implements Variable Elimination (VE), an exact inferencing algorithm that employs
 * the sparse dependency structure of graphical models to reduce complexity.
 * 
 * @author Thorben
 */
public class VariableEliminationInferencer implements ExactInferencer {

  protected GraphicalModel graphicalModel;
  protected VariableEliminationStrategy variableEliminationStrategy;
  
  public VariableEliminationInferencer(GraphicalModel graphicalModel, VariableEliminationStrategy variableEliminationStrategy) {
    this.graphicalModel = graphicalModel;
    this.variableEliminationStrategy = variableEliminationStrategy;
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
  
  protected GraphicalModel reduceModelByObservations(Scope observedVariables, int[] observation) {
    Scope newScope = graphicalModel.getScope().reduceBy(observedVariables);
    
    Set<DiscreteFactor> newFactors = new HashSet<DiscreteFactor>();
    
    for (DiscreteFactor factor : graphicalModel.getFactors()) {
      DiscreteFactor factorWithObservation = factor.observation(observedVariables, observation);
      DiscreteFactor marginalizedFactor = factorWithObservation.marginal(newScope);
      newFactors.add(marginalizedFactor);
    }
    
    GraphicalModel newModel = new GraphicalModel(newScope, newFactors);
    return newModel;
  }
  
  protected DiscreteFactor jointProbabilityDistribution(Scope projection, Scope observedVariables, int[] observation) {
    // 1. reduce model by observations (i.e. effectively remove the variables that are observed from the new model)
    GraphicalModel reducedModel = graphicalModel;
    if (observedVariables != null && !observedVariables.isEmpty()) {
      reducedModel = reduceModelByObservations(observedVariables, observation);
    }
    
    // 2. determine a variable elimination order for the new model
    Collection<String> variablesToEliminate = Arrays.asList(reducedModel.getScope().reduceBy(projection).getVariableIds());
    List<String> variableEliminationOrder = variableEliminationStrategy.getEliminationOrder(reducedModel, variablesToEliminate);
    validateEliminationOrder(reducedModel, projection, variableEliminationOrder);
    
    Set<DiscreteFactor> factors = reducedModel.getFactors();
    
    // 3. Eliminate variables according to the order
    for (String variableToEliminate : variableEliminationOrder) {
      Set<DiscreteFactor> factorsWithVariable = factorsWithVariableInScope(factors, variableToEliminate);
      factors.removeAll(factorsWithVariable);
      
      DiscreteFactor jointDistribution = FactorUtil.jointDistribution(factorsWithVariable);
      DiscreteFactor marginalizedDistribution = jointDistribution
          .marginal(jointDistribution.getVariables().reduceBy(variableToEliminate));
      
      factors.add(marginalizedDistribution);
    }
    
    // 4. Create joint distribution from remaining factors
    return FactorUtil.jointDistribution(factors);
  }
  
  // TODO: make this method obsolete by using a more appropriate data structure for factors 
  // that allows efficient lookup by variables
  protected Set<DiscreteFactor> factorsWithVariableInScope(Set<DiscreteFactor> factors, String variable) {
    Set<DiscreteFactor> result = new HashSet<DiscreteFactor>();
    
    for (DiscreteFactor factor : factors) {
      if (factor.getVariables().has(variable)) {
        result.add(factor);
      }
    }
    
    return result;
  }
  
  protected void validateEliminationOrder(GraphicalModel model, Scope projection, List<String> variableEliminationOrder) {
    for (DiscreteVariable modelVariable : model.getScope().getVariables()) {
      boolean isProjectionVariable = projection.has(modelVariable);
      boolean isVariableToBeEliminated = variableEliminationOrder.contains(modelVariable.getId());
      
      if (!isProjectionVariable && !isVariableToBeEliminated) {
        throw new InferenceException("Model variable " + modelVariable.getId() + " is neither in the joint distribution's scope," +
        		" nor in the variables to be eliminated.");
      }
      
      if (isProjectionVariable && isVariableToBeEliminated) {
        throw new InferenceException("Model variable " + modelVariable.getId() + " is supposed to be part of the joint probability" +
        		" distribution, as well as to be eliminated.");
      }
    }
  }

}
