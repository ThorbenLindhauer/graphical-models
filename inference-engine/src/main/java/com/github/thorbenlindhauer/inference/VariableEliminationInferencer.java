package com.github.thorbenlindhauer.inference;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.thorbenlindhauer.exception.InferenceException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.inference.variableelimination.VariableEliminationStrategy;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.IndexCoder;
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
    Collection<String> variablesToEliminate = graphicalModel.getScope().removeAll(projection).getVariableIds();
    List<String> variableEliminationOrder = variableEliminationStrategy.getEliminationOrder(graphicalModel, variablesToEliminate);
    return jointProbabilityDistribution(projection, observedVariables, observation, variableEliminationOrder).getValueForAssignment(variableAssignment);
  }

  public double jointProbabilityConditionedOn(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Collection<String> variablesToEliminate = graphicalModel.getScope().removeAll(projection).getVariableIds();
    List<String> variableEliminationOrder = variableEliminationStrategy.getEliminationOrder(graphicalModel, variablesToEliminate);
    
    DiscreteFactor jointMarginalDistribution = jointProbabilityDistribution(projection, observedVariables, observation, variableEliminationOrder);
    DiscreteFactor normalizedDistribution = jointMarginalDistribution.normalize();
    return normalizedDistribution.getValueForAssignment(variableAssignment);
  }
  
  protected DiscreteFactor jointProbabilityDistribution(Scope projection, Scope observedVariables, int[] observation, List<String> variableEliminationOrder) {
    validateEliminationOrder(projection, variableEliminationOrder);
    
    Set<DiscreteFactor> factors = graphicalModel.getFactors();
    Set<DiscreteFactor> factorsWithObservations = processObservations(factors, observedVariables, observation);
    
    for (String variableToEliminate : variableEliminationOrder) {
      Set<DiscreteFactor> factorsWithVariable = factorsWithVariableInScope(factorsWithObservations, variableToEliminate);
      factorsWithObservations.removeAll(factorsWithVariable);
      
      DiscreteFactor jointDistribution = FactorUtil.jointDistribution(factorsWithVariable);
      DiscreteFactor marginalizedDistribution = jointDistribution
          .marginal(jointDistribution.getVariables().removeAll(variableToEliminate));
      
      factorsWithObservations.add(marginalizedDistribution);
    }
    
    return FactorUtil.jointDistribution(factorsWithObservations);
  }
  
  protected Set<DiscreteFactor> processObservations(Set<DiscreteFactor> factors, Scope observedVariables, int[] observation) {
    if (observedVariables == null || observedVariables.isEmpty()) {
      return new HashSet<DiscreteFactor>(factors);
    }
    
    Set<DiscreteFactor> replacements = new HashSet<DiscreteFactor>();
    Set<DiscreteFactor> removals = new HashSet<DiscreteFactor>();
    
    
    Iterator<DiscreteFactor> it = factors.iterator();
    
    while (it.hasNext()) {
      DiscreteFactor factor = it.next();
      
      BitSet observedVariableToFactorScopeProjection = observedVariables.getProjection(factor.getVariables());
      
      if (observedVariableToFactorScopeProjection.cardinality() > 0) {
        Scope reducedObservedVariables = observedVariables.intersect(factor.getVariables());
        int[] reducedObservation = IndexCoder.projectAssignment(observation, observedVariableToFactorScopeProjection);
        
        replacements.add(factor.observation(reducedObservedVariables, reducedObservation));
        removals.add(factor);
      }
    }
    
    Set<DiscreteFactor> result = new HashSet<DiscreteFactor>();
    result.addAll(factors);
    result.removeAll(removals);
    result.addAll(replacements);
    
    return result;
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
  
  protected void validateEliminationOrder(Scope projection, List<String> variableEliminationOrder) {
    for (DiscreteVariable modelVariable : graphicalModel.getScope().getVariables()) {
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
