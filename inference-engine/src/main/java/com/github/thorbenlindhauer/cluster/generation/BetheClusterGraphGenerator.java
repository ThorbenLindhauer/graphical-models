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
package com.github.thorbenlindhauer.cluster.generation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.factor.DefaultFactorFactory;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

/**
 * @author Thorben
 *
 */
public class BetheClusterGraphGenerator {

  public <T extends Factor<T>> ClusterGraph<T> generateClusterGraph(GraphicalModel<T> graphicalModel, DefaultFactorFactory<T> defaultFactorFactory) {
    Set<Cluster<T>> clusters = new HashSet<Cluster<T>>();

    // create a cluster for each variable
    Map<String, Cluster<T>> variableClusters = new HashMap<String, Cluster<T>>();
    for (Variable variable : graphicalModel.getScope().getVariables()) {
      Scope scope = new Scope(Arrays.asList(variable));
      T defaultFactor = defaultFactorFactory.build(scope);
      Cluster<T> variableCluster = new Cluster<T>(Collections.singleton(defaultFactor));
      clusters.add(variableCluster);
      variableClusters.put(variable.getId(), variableCluster);
    }

    // create a cluster for each factor
    Set<Cluster<T>> factorClusters = new HashSet<Cluster<T>>();
    for (T factor : graphicalModel.getFactors()) {
      Cluster<T> factorCluster = new Cluster<T>(Collections.singleton(factor));
      clusters.add(factorCluster);
      factorClusters.add(factorCluster);
    }

    ClusterGraph<T> clusterGraph = new ClusterGraph<T>(clusters);

    for (Cluster<T> factorCluster : factorClusters) {
      for (String variableId : factorCluster.getScope().getVariableIds()) {
        clusterGraph.connect(factorCluster, variableClusters.get(variableId));
      }
    }

    return clusterGraph;
  }
}
