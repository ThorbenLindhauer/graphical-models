package com.github.thorbenlindhauer.graph.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.condition.AnyOf.anyOf;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Test;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterEdgeCondition;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class MaximumSpanningClusterGraphOperationTest {

  protected Set<Cluster> clusters;
  
  @Test
  public void testClusterMaximumSpanningTree() {
    // given
    Cluster cdCluster = newCluster("C", "D");
    Cluster digCluster = newCluster("D", "I", "G");
    Cluster gisCluster = newCluster("G", "I", "S");
    Cluster glsCluster = newCluster("G", "L", "S");
    Cluster jlsCluster = newCluster("J", "L", "S");
    Cluster ghCluster = newCluster("G", "H");

    LinkedHashSet<Cluster> clusters = Sets.newLinkedHashSet(cdCluster, digCluster, gisCluster, glsCluster, jlsCluster, ghCluster);
    
    // when
    MaximumSpanningClusterGraphOperation spanningTreeAlg = new MaximumSpanningClusterGraphOperation();
    ClusterGraph clusterGraph = spanningTreeAlg.execute(clusters);
    
    // then the edges correspond to the maximum spanning tree
    Set<Edge> edges = clusterGraph.getEdges();
    
    assertThat(edges).hasSize(5);
    assertThat(edges).areExactly(1, new ClusterEdgeCondition(cdCluster, digCluster));
    assertThat(edges).areExactly(1, new ClusterEdgeCondition(digCluster, gisCluster));
    assertThat(edges).areExactly(1, new ClusterEdgeCondition(gisCluster, glsCluster));
    assertThat(edges).areExactly(1, new ClusterEdgeCondition(glsCluster, jlsCluster));
    
    // the connection of gh to the other cluster is not deterministically specified;
    // candidates could be dig, gis, gls
    assertThat(edges).areExactly(1, anyOf(
        new ClusterEdgeCondition(ghCluster, digCluster),
        new ClusterEdgeCondition(ghCluster, gisCluster),
        new ClusterEdgeCondition(ghCluster, glsCluster)));
  }
  
  protected Cluster newCluster(String... variableIds) {
    Set<DiscreteVariable> variables = new HashSet<DiscreteVariable>();
    
    for (String variableId : variableIds) {
      variables.add(new DiscreteVariable(variableId, 1));
    }
    
    Scope scope = new Scope(variables);
    return new Cluster(scope);
  }
}
