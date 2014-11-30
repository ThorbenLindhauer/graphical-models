/* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
    Scope jointDistributionScope = projection;
    if (observedVariables != null) {
      jointDistributionScope = jointDistributionScope.union(observedVariables);
    }

    DiscreteFactor jointDistribution = jointProbabilityDistribution(jointDistributionScope).normalize();
    if (observedVariables != null) {
      jointDistribution = jointDistribution.observation(observedVariables, observation);
    }
    return jointDistribution.marginal(projection).getValueForAssignment(variableAssignment);
  }

  public double jointProbabilityConditionedOn(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Scope jointDistributionScope = projection;
    if (observedVariables != null) {
      jointDistributionScope = jointDistributionScope.union(observedVariables);
    }

    DiscreteFactor jointDistribution = jointProbabilityDistribution(jointDistributionScope);

    if (observedVariables != null) {
      jointDistribution = jointDistribution.observation(observedVariables, observation);
    }

    DiscreteFactor normalizedDistribution = jointDistribution.normalize();
    return normalizedDistribution.marginal(projection).getValueForAssignment(variableAssignment);
  }

  protected DiscreteFactor jointProbabilityDistribution(Scope scope) {
    // 1. determine a variable elimination order for the new model
    Scope scopeToEliminate = graphicalModel.getScope().reduceBy(scope);
    Collection<String> variablesToEliminate = Arrays.asList(scopeToEliminate.getVariableIds());
    List<String> variableEliminationOrder = variableEliminationStrategy.getEliminationOrder(graphicalModel, variablesToEliminate);
    validateEliminationOrder(graphicalModel, scope, variableEliminationOrder);

    Set<DiscreteFactor> factors = graphicalModel.getFactors();

    // 2. Eliminate variables according to the order
    for (String variableToEliminate : variableEliminationOrder) {
      Set<DiscreteFactor> factorsWithVariable = factorsWithVariableInScope(factors, variableToEliminate);
      factors.removeAll(factorsWithVariable);

      DiscreteFactor jointDistribution = FactorUtil.jointDistribution(factorsWithVariable);
      DiscreteFactor marginalizedDistribution = jointDistribution
          .marginal(jointDistribution.getVariables().reduceBy(variableToEliminate));

      factors.add(marginalizedDistribution);
    }

    // 3. Create joint distribution from remaining factors
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

  protected void validateEliminationOrder(GraphicalModel model, Scope scope, List<String> variableEliminationOrder) {
    for (DiscreteVariable modelVariable : model.getScope().getVariables()) {
      boolean isProjectionVariable = scope.has(modelVariable);
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
