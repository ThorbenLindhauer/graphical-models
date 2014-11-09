package com.github.thorbenlindhauer.graph.operation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraphEdge;
import com.github.thorbenlindhauer.factorgraph.FactorGraphEdgeCondition;
import com.github.thorbenlindhauer.factorgraph.FactorGraphNode;
import com.github.thorbenlindhauer.variable.DiscreteVariable;

public class TriangulatorTest {

  @Test
  public void testTriangulation() {
    // given
    Set<FactorGraphNode> nodes = new HashSet<FactorGraphNode>();
    FactorGraphNode aNode = new FactorGraphNode(new DiscreteVariable("A", 1));
    FactorGraphNode bNode = new FactorGraphNode(new DiscreteVariable("B", 1));
    FactorGraphNode cNode = new FactorGraphNode(new DiscreteVariable("C", 1));
    FactorGraphNode dNode = new FactorGraphNode(new DiscreteVariable("D", 1));
    FactorGraphNode eNode = new FactorGraphNode(new DiscreteVariable("E", 1));
    nodes.add(aNode);
    nodes.add(bNode);
    nodes.add(cNode);
    nodes.add(dNode);
    nodes.add(eNode);
    
    Set<FactorGraphEdge> edges = new HashSet<FactorGraphEdge>();
    edges.add(aNode.connectTo(bNode));
    edges.add(aNode.connectTo(cNode));
    edges.add(bNode.connectTo(dNode));
    edges.add(cNode.connectTo(dNode));
    edges.add(dNode.connectTo(eNode));
    
    FactorGraph factorGraph = new FactorGraph(nodes, edges);
    
    // when
    Triangulator triangulator = new Triangulator();
    FactorGraph inducedGraph = triangulator.getInducedGraph(factorGraph, Arrays.asList("A", "D", "B", "E", "C"));
    
    // then
    // 8 edges in induced graph
    Set<FactorGraphEdge> inducedEdges = new HashSet<FactorGraphEdge>(inducedGraph.getEdges());
    assertThat(inducedEdges).hasSize(8);
    
    // 3 fill edges
    inducedEdges.removeAll(edges);
    assertThat(inducedEdges).hasSize(3);
    assertThat(inducedEdges).areExactly(1, new FactorGraphEdgeCondition(cNode, bNode));
    assertThat(inducedEdges).areExactly(1, new FactorGraphEdgeCondition(cNode, eNode));
    assertThat(inducedEdges).areExactly(1, new FactorGraphEdgeCondition(bNode, eNode));
  }
}
