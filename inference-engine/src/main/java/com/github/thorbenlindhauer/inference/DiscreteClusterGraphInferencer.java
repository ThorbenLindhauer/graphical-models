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

import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.inference.loopy.ClusterGraphCalibrationContextFactory;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public class DiscreteClusterGraphInferencer extends AbstractClusterGraphInferencer<DiscreteFactor>
  implements DiscreteModelInferencer {


  public DiscreteClusterGraphInferencer(ClusterGraph<DiscreteFactor> clusterGraph, MessagePassingContextFactory messageContextFactory,
      ClusterGraphCalibrationContextFactory<DiscreteFactor> calibrationContextFactory) {
    super(clusterGraph, messageContextFactory, calibrationContextFactory);
  }

  public double jointProbability(Scope projection, int[] variableAssignment) {
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(projection);
    return matchingFactor.marginal(projection).normalize().getValueForAssignment(variableAssignment);
  }

  public double jointProbability(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Scope jointScope = projection.union(observedVariables);
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(jointScope);
    return matchingFactor.normalize().observation(observedVariables, observation).marginal(projection).getValueForAssignment(variableAssignment);
  }

  public double jointProbabilityConditionedOn(Scope projection, int[] variableAssignment, Scope observedVariables, int[] observation) {
    Scope jointScope = projection.union(observedVariables);
    DiscreteFactor matchingFactor = getClusterFactorContainingScope(jointScope);
    return matchingFactor.observation(observedVariables, observation).marginal(projection).normalize().getValueForAssignment(variableAssignment);
  }
}
