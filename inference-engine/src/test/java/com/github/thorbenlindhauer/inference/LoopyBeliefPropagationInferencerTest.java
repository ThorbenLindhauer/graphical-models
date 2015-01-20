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
import com.github.thorbenlindhauer.cluster.generation.BetheClusterGraphGenerator;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.DefaultFactorFactory.DefaultDiscreteFactorFactory;
import com.github.thorbenlindhauer.inference.loopy.ClusterGraphCalibrationContextFactory;
import com.github.thorbenlindhauer.network.GraphicalModel;

public abstract class LoopyBeliefPropagationInferencerTest extends ExactInferencerTest {

  @Override
  protected DiscreteModelInferencer getInferencer(GraphicalModel<DiscreteFactor> graphicalModel) {
    ClusterGraph<DiscreteFactor> clusterGraph = new BetheClusterGraphGenerator().generateClusterGraph(graphicalModel, new DefaultDiscreteFactorFactory());
    return new DiscreteClusterGraphInferencer(clusterGraph, getMessagePassingContextFactory(), getCalibrationContextFactory());
  }

  protected abstract MessagePassingContextFactory getMessagePassingContextFactory();

  protected abstract ClusterGraphCalibrationContextFactory<DiscreteFactor> getCalibrationContextFactory();

}
