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

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.generation.BetheClusterGraphGenerator;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.exception.InferenceException;
import com.github.thorbenlindhauer.factor.DefaultFactorFactory.DefaultDiscreteFactorFactory;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.network.GraphicalModel;

public abstract class AbstractCliqueTreeInferencerTest extends ExactInferencerTest {

  @Override
  protected ExactInferencer getInferencer(GraphicalModel<DiscreteFactor> graphicalModel) {
    ClusterGraph<DiscreteFactor> clusterGraph = buildClusterGraph(graphicalModel);
    Cluster<DiscreteFactor> rootCluster = determineRootCluster(graphicalModel, clusterGraph);
    return new CliqueTreeInferencer(clusterGraph, rootCluster, getMessagePassingContextFactory());
  }

  protected Cluster<DiscreteFactor> determineRootCluster(GraphicalModel<DiscreteFactor> graphicalModel, ClusterGraph<DiscreteFactor> clusterGraph) {
    String[] rootClusterScope = null;

    if (graphicalModel == bayesianNetwork) {
      rootClusterScope = new String[]{ "A", "B", "C" };
    } else if (graphicalModel == markovNetwork) {
      rootClusterScope = new String[]{ "B", "C" };
    }

    for (Cluster<DiscreteFactor> factorCluster : clusterGraph.getClusters()) {
      // we select the cluster representing the factor over A, B, C as the root cluster
      if (factorCluster.getScope().contains(rootClusterScope)) {
        return factorCluster;
      }
    }

    throw new InferenceException("Could not find root cluster");
  }

  protected ClusterGraph<DiscreteFactor> buildClusterGraph(GraphicalModel<DiscreteFactor> graphicalModel) {
    // constructs a bethe cluster graph
    // in general, a bethe graph is not a tree but for this test case it is
    return new BetheClusterGraphGenerator().generateClusterGraph(graphicalModel, new DefaultDiscreteFactorFactory());
  }


  protected abstract MessagePassingContextFactory getMessagePassingContextFactory();

}
