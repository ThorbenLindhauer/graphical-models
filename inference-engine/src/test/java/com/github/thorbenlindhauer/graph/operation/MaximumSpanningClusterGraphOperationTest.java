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
package com.github.thorbenlindhauer.graph.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.condition.AnyOf.anyOf;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Test;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterEdgeCondition;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DefaultFactorFactory.DefaultDiscreteFactorFactory;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class MaximumSpanningClusterGraphOperationTest {

  protected Set<Cluster<DiscreteFactor>> clusters;

  @SuppressWarnings("unchecked")
  @Test
  public void testClusterMaximumSpanningTree() {
    // given
    Cluster<DiscreteFactor> cdCluster = newCluster("C", "D");
    Cluster<DiscreteFactor> digCluster = newCluster("D", "I", "G");
    Cluster<DiscreteFactor> gisCluster = newCluster("G", "I", "S");
    Cluster<DiscreteFactor> glsCluster = newCluster("G", "L", "S");
    Cluster<DiscreteFactor> jlsCluster = newCluster("J", "L", "S");
    Cluster<DiscreteFactor> ghCluster = newCluster("G", "H");

    LinkedHashSet<Cluster<DiscreteFactor>> clusters = Sets.newLinkedHashSet(cdCluster, digCluster, gisCluster, glsCluster, jlsCluster, ghCluster);

    // when
    MaximumSpanningClusterGraphOperation spanningTreeAlg = new MaximumSpanningClusterGraphOperation();
    ClusterGraph<DiscreteFactor> clusterGraph = spanningTreeAlg.execute(clusters);

    // then the edges correspond to the maximum spanning tree
    Set<Edge<DiscreteFactor>> edges = clusterGraph.getEdges();

    assertThat(edges).hasSize(5);
    assertThat(edges).areExactly(1, new ClusterEdgeCondition<DiscreteFactor>(cdCluster, digCluster));
    assertThat(edges).areExactly(1, new ClusterEdgeCondition<DiscreteFactor>(digCluster, gisCluster));
    assertThat(edges).areExactly(1, new ClusterEdgeCondition<DiscreteFactor>(gisCluster, glsCluster));
    assertThat(edges).areExactly(1, new ClusterEdgeCondition<DiscreteFactor>(glsCluster, jlsCluster));

    // the connection of gh to the other cluster is not deterministically specified;
    // candidates could be dig, gis, gls
    assertThat(edges).areExactly(1, anyOf(
        new ClusterEdgeCondition<DiscreteFactor>(ghCluster, digCluster),
        new ClusterEdgeCondition<DiscreteFactor>(ghCluster, gisCluster),
        new ClusterEdgeCondition<DiscreteFactor>(ghCluster, glsCluster)));
  }

  protected Cluster<DiscreteFactor> newCluster(String... variableIds) {
    Set<DiscreteVariable> variables = new HashSet<DiscreteVariable>();

    for (String variableId : variableIds) {
      variables.add(new DiscreteVariable(variableId, 1));
    }

    Scope scope = new Scope(variables);
    DiscreteFactor factor = new DefaultDiscreteFactorFactory().build(scope);
    return new Cluster<DiscreteFactor>(Collections.singleton(factor));
  }
}
