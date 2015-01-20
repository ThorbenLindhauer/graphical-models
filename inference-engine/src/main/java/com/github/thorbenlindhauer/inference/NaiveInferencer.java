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

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Most simple inferencer that just builds the full joint probability distribution.
 *
 * @author Thorben
 */
public class NaiveInferencer implements DiscreteModelInferencer {

  protected GraphicalModel<DiscreteFactor> model;
  protected DiscreteFactor jointDistribution;

  public NaiveInferencer(GraphicalModel<DiscreteFactor> model) {
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
      jointDistribution = FactorUtil.jointDistribution(model.getFactors()).normalize();
    }
  }

}
