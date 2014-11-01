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
    Cluster cluster1 = new Cluster(Sets.newLinkedHashSet(factor1));
    Cluster cluster2 = new Cluster(Sets.newLinkedHashSet(factor2));
    Cluster cluster3 = new Cluster(Sets.newLinkedHashSet(factor3));
    Cluster cluster4 = new Cluster(Sets.newLinkedHashSet(factor1, factor2));
    
    assertThat(cluster1.getScope().getVariableIds()).containsExactly("A");
    assertThat(cluster2.getScope().getVariableIds()).containsExactly("B");
    assertThat(cluster3.getScope().getVariableIds()).containsExactly("A", "B", "C");
    assertThat(cluster4.getScope().getVariableIds()).containsExactly("A", "B");
  }
  
  @Test
  public void testEdgeCreation() {
    Cluster cluster1 = new Cluster(Sets.newLinkedHashSet(factor1));
    Cluster cluster2 = new Cluster(Sets.newLinkedHashSet(factor2));
    Cluster cluster3 = new Cluster(Sets.newLinkedHashSet(factor3));
    
    Edge edge = cluster1.connectTo(cluster2);
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
  
  @Test
  public void testEdgeCreationInClusterGraph() {
    Cluster cluster1 = new Cluster(Sets.newLinkedHashSet(factor1));
    Cluster cluster2 = new Cluster(Sets.newLinkedHashSet(factor2));
    
    ClusterGraph graph = new ClusterGraph(Sets.newLinkedHashSet(cluster1, cluster2));
    
    Edge edge = graph.connect(cluster1, cluster2);
    assertThat(edge.getTarget(cluster1)).isEqualTo(cluster2);
    assertThat(edge.getTarget(cluster2)).isEqualTo(cluster1);
    assertThat(graph.getEdges()).contains(edge);
    
    Cluster cluster3 = new Cluster(Sets.newLinkedHashSet(factor3));
    try {
      graph.connect(cluster1, cluster3);
      fail("expected exception as cluster3 is not part of the graph");
    } catch (ModelStructureException e) {
      // happy path
    }
  }
}
