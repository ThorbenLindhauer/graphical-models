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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraphEdge;
import com.github.thorbenlindhauer.factorgraph.FactorGraphEdgeCondition;
import com.github.thorbenlindhauer.factorgraph.FactorGraphNode;
import com.github.thorbenlindhauer.variable.DiscreteVariable;

public class TriangulatorTest {

  @Test
  public void testTriangulation() {
    // given
    Set<FactorGraphNode<DiscreteFactor>> nodes = new HashSet<FactorGraphNode<DiscreteFactor>>();
    FactorGraphNode<DiscreteFactor> aNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("A", 1));
    FactorGraphNode<DiscreteFactor> bNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("B", 1));
    FactorGraphNode<DiscreteFactor> cNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("C", 1));
    FactorGraphNode<DiscreteFactor> dNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("D", 1));
    FactorGraphNode<DiscreteFactor> eNode = new FactorGraphNode<DiscreteFactor>(new DiscreteVariable("E", 1));
    nodes.add(aNode);
    nodes.add(bNode);
    nodes.add(cNode);
    nodes.add(dNode);
    nodes.add(eNode);

    Set<FactorGraphEdge<DiscreteFactor>> edges = new HashSet<FactorGraphEdge<DiscreteFactor>>();
    edges.add(aNode.connectTo(bNode));
    edges.add(aNode.connectTo(cNode));
    edges.add(bNode.connectTo(dNode));
    edges.add(cNode.connectTo(dNode));
    edges.add(dNode.connectTo(eNode));

    FactorGraph<DiscreteFactor> factorGraph = new FactorGraph<DiscreteFactor>(nodes, edges);

    // when
    Triangulator triangulator = new Triangulator();
    FactorGraph<DiscreteFactor> inducedGraph = triangulator.getInducedGraph(factorGraph, Arrays.asList("A", "D", "B", "E", "C"));

    // then
    // 8 edges in induced graph
    Set<FactorGraphEdge<DiscreteFactor>> inducedEdges = new HashSet<FactorGraphEdge<DiscreteFactor>>(inducedGraph.getEdges());
    assertThat(inducedEdges).hasSize(8);

    // 3 fill edges
    inducedEdges.removeAll(edges);
    assertThat(inducedEdges).hasSize(3);
    assertThat(inducedEdges).areExactly(1, new FactorGraphEdgeCondition<DiscreteFactor>(cNode, bNode));
    assertThat(inducedEdges).areExactly(1, new FactorGraphEdgeCondition<DiscreteFactor>(cNode, eNode));
    assertThat(inducedEdges).areExactly(1, new FactorGraphEdgeCondition<DiscreteFactor>(bNode, eNode));
  }
}
