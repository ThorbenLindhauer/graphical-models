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

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterFactorCondition;
import com.github.thorbenlindhauer.cluster.ClusterScopeCondition;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraphEdge;
import com.github.thorbenlindhauer.factorgraph.FactorGraphNode;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class MaximumCardinalityCliqueAnalyzerTest {

  protected FactorGraph<DiscreteFactor> factorGraph;
  protected Set<DiscreteFactor> factors;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() {
    Set<FactorGraphNode<DiscreteFactor>> nodes = new HashSet<FactorGraphNode<DiscreteFactor>>();
    FactorGraphNode<DiscreteFactor> cNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("C", 1));  // cardinality does not matter
    FactorGraphNode<DiscreteFactor> dNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("D", 1));
    FactorGraphNode<DiscreteFactor> iNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("I", 1));
    FactorGraphNode<DiscreteFactor> gNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("G", 1));
    FactorGraphNode<DiscreteFactor> sNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("S", 1));
    FactorGraphNode<DiscreteFactor> lNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("L", 1));
    FactorGraphNode<DiscreteFactor> jNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("J", 1));
    FactorGraphNode<DiscreteFactor> hNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("H", 1));

    factors = new HashSet<DiscreteFactor>();
    factors.add(newFactor(new String[]{"C"}, cNode));
    factors.add(newFactor(new String[]{"C", "D"}, cNode, dNode));
    factors.add(newFactor(new String[]{"I"}, iNode));
    factors.add(newFactor(new String[]{"D", "I", "G"}, dNode, iNode, gNode));
    factors.add(newFactor(new String[]{"I", "S"}, iNode, sNode));
    factors.add(newFactor(new String[]{"G", "H"}, gNode, hNode));
    factors.add(newFactor(new String[]{"G", "L", "S"}, gNode, lNode, sNode));
    factors.add(newFactor(new String[]{"J", "L", "S"}, jNode, lNode, sNode));

    nodes.add(cNode);
    nodes.add(dNode);
    nodes.add(iNode);
    nodes.add(gNode);
    nodes.add(sNode);
    nodes.add(lNode);
    nodes.add(jNode);
    nodes.add(hNode);

    Set<FactorGraphEdge<DiscreteFactor>> edges = new HashSet<FactorGraphEdge<DiscreteFactor>>();
    edges.add(cNode.connectTo(dNode));
    edges.add(dNode.connectTo(iNode));
    edges.add(dNode.connectTo(gNode));
    edges.add(iNode.connectTo(gNode));
    edges.add(iNode.connectTo(sNode));
    edges.add(gNode.connectTo(sNode));
    edges.add(gNode.connectTo(hNode));
    edges.add(gNode.connectTo(lNode));
    edges.add(sNode.connectTo(lNode));
    edges.add(sNode.connectTo(jNode));
    edges.add(lNode.connectTo(jNode));

    factorGraph = new FactorGraph<DiscreteFactor>(nodes, edges);
  }

  protected DiscreteFactor newFactor(String[] variableIds, FactorGraphNode<DiscreteFactor>... nodes) {
    Set<DiscreteVariable> variables = new HashSet<DiscreteVariable>();

    for(String variableId : variableIds) {
      variables.add(new DiscreteVariable(variableId, 1));
    }

    Scope scope = new Scope(variables);
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(scope, new double[]{0});

    for (FactorGraphNode<DiscreteFactor> node : nodes) {
      node.addFactor(factor);
    }

    return factor;
  }

  @Test
  public void testCliqueCreation() {
    MaximumCardinalityCliqueOperation<DiscreteFactor> cliqueTreeAnalyzer = new MaximumCardinalityCliqueOperation<DiscreteFactor>();
    Set<Cluster<DiscreteFactor>> clusters = cliqueTreeAnalyzer.execute(factorGraph);

    assertThat(clusters).hasSize(6);
    assertThat(clusters).areExactly(1, new ClusterScopeCondition(new String[] {"C", "D"}));
    assertThat(clusters).areExactly(1, new ClusterScopeCondition(new String[] {"D", "I", "G"}));
    assertThat(clusters).areExactly(1, new ClusterScopeCondition(new String[] {"G", "I", "S"}));
    assertThat(clusters).areExactly(1, new ClusterScopeCondition(new String[] {"G", "L", "S"}));
    assertThat(clusters).areExactly(1, new ClusterScopeCondition(new String[] {"J", "L", "S"}));
    assertThat(clusters).areExactly(1, new ClusterScopeCondition(new String[] {"G", "H"}));
  }

  @Test
  public void testClusterAssignmentFamilyPreservation() {
    MaximumCardinalityCliqueOperation<DiscreteFactor> cliqueTreeAnalyzer = new MaximumCardinalityCliqueOperation<DiscreteFactor>();
    Set<Cluster<DiscreteFactor>> clusters = cliqueTreeAnalyzer.execute(factorGraph);

    for (DiscreteFactor factor : factors) {
      assertThat(clusters).areExactly(1, new ClusterFactorCondition<DiscreteFactor>(factor));
    }

  }
}
