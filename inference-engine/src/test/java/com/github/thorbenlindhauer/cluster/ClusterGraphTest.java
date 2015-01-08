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
package com.github.thorbenlindhauer.cluster;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import java.util.HashSet;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class ClusterGraphTest {

  protected DiscreteFactor factor1;
  protected DiscreteFactor factor2;
  protected DiscreteFactor factor3;

  @Before
  public void setUp() {
    Set<DiscreteVariable> variables = new HashSet<DiscreteVariable>();
    variables.add(new DiscreteVariable("A", 1));
    variables.add(new DiscreteVariable("B", 1));
    variables.add(new DiscreteVariable("C", 1));

    Scope overallScope = new Scope(variables);

    factor1 = new TableBasedDiscreteFactor(overallScope.subScope("A"), new double[]{1});
    factor2 = new TableBasedDiscreteFactor(overallScope.subScope("B"), new double[] {1});
    factor3 = new TableBasedDiscreteFactor(overallScope.subScope("A", "B", "C"), new double[] {1});
  }

  @Test
  public void testClusterCreation() {
    Cluster<DiscreteFactor> cluster1 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor1));
    Cluster<DiscreteFactor> cluster2 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor2));
    Cluster<DiscreteFactor> cluster3 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor3));
    Cluster<DiscreteFactor> cluster4 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor1, factor2));

    assertThat(cluster1.getScope().getVariableIds()).containsExactly("A");
    assertThat(cluster2.getScope().getVariableIds()).containsExactly("B");
    assertThat(cluster3.getScope().getVariableIds()).containsExactly("A", "B", "C");
    assertThat(cluster4.getScope().getVariableIds()).containsExactly("A", "B");
  }

  @Test
  public void testEdgeCreation() {
    Cluster<DiscreteFactor> cluster1 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor1));
    Cluster<DiscreteFactor> cluster2 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor2));
    Cluster<DiscreteFactor> cluster3 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor3));

    Edge<DiscreteFactor> edge = cluster1.connectTo(cluster2);
    assertThat(edge.getTarget(cluster1)).isEqualTo(cluster2);
    assertThat(edge.getTarget(cluster2)).isEqualTo(cluster1);

    try {
      edge.getTarget(cluster3);
      fail("exception expected");
    } catch (ModelStructureException e) {
      // expected
    }

    assertThat(edge.getScope().getVariableIds()).isEmpty();

    edge = cluster1.connectTo(cluster3);
    assertThat(edge.getTarget(cluster1)).isEqualTo(cluster3);
    assertThat(edge.getTarget(cluster3)).isEqualTo(cluster1);

    assertThat(edge.getScope().getVariableIds()).containsExactly("A");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testEdgeCreationInClusterGraph() {
    Cluster<DiscreteFactor> cluster1 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor1));
    Cluster<DiscreteFactor> cluster2 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor2));

    ClusterGraph<DiscreteFactor> graph = new ClusterGraph<DiscreteFactor>(Sets.newLinkedHashSet(cluster1, cluster2));

    Edge<DiscreteFactor> edge = graph.connect(cluster1, cluster2);
    assertThat(edge.getTarget(cluster1)).isEqualTo(cluster2);
    assertThat(edge.getTarget(cluster2)).isEqualTo(cluster1);
    assertThat(graph.getEdges()).contains(edge);

    Cluster<DiscreteFactor> cluster3 = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(factor3));
    try {
      graph.connect(cluster1, cluster3);
      fail("expected exception as cluster3 is not part of the graph");
    } catch (ModelStructureException e) {
      // happy path
    }
  }
}
