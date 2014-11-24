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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public abstract class AbstractCliqueTreeInferencerTest extends ExactInferencerTest {

  protected ClusterGraph clusterGraph;
  protected Cluster rootCluster;
  
  @Before
  public void setUpClusterGraph() {
    Set<Cluster> clusters = new HashSet<Cluster>();

    // constructs a bethe cluster graph
    // in general, a bethe graph is not a tree but for this test case it is
    
    // create a cluster for each variable
    Map<String, Cluster> variableClusters = new HashMap<String, Cluster>();
    for (DiscreteVariable variable : model.getScope().getVariables()) {
      Cluster variableCluster = new Cluster(new Scope(Arrays.asList(variable)));
      clusters.add(variableCluster);
      variableClusters.put(variable.getId(), variableCluster);
    }
    
    // create a cluster for each factor
    Set<Cluster> factorClusters = new HashSet<Cluster>();
    for (DiscreteFactor factor : model.getFactors()) {
      Cluster factorCluster = new Cluster(Sets.newLinkedHashSet(factor));
      clusters.add(factorCluster);
      factorClusters.add(factorCluster);
    }
    
    clusterGraph = new ClusterGraph(clusters);
    
    for (Cluster factorCluster : factorClusters) {
      for (String variableId : factorCluster.getScope().getVariableIds()) {
        clusterGraph.connect(factorCluster, variableClusters.get(variableId));
      }
      
      // we select the cluster representing the factor over A, B, C as the root cluster
      if (factorCluster.getScope().contains("A", "B", "C")) {
        rootCluster = factorCluster;
      }
    }
    
  }
  
  @Override
  protected ExactInferencer getInferencer() {
    return new CliqueTreeInferencer(clusterGraph, rootCluster, getMessagePassingContextFactory());
  }
  
  protected abstract MessagePassingContextFactory getMessagePassingContextFactory();

}
