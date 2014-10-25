package com.github.thorbenlindhauer.inference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;

import com.github.thorbenlindhauer.cluster.messagepassing.SumProductCluster;
import com.github.thorbenlindhauer.cluster.messagepassing.SumProductClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.SumProductEdge;
import com.github.thorbenlindhauer.cluster.messagepassing.SumProductMessage;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class SumProductCliqueTreeInferencerTest extends ExactInferencerTest {

  protected SumProductClusterGraph clusterGraph;
  protected SumProductCluster rootCluster;
  
  @Before
  public void setUpClusterGraph() {
    Set<SumProductCluster> clusters = new HashSet<SumProductCluster>();

    // constructs a bethe cluster graph
    // in general, a bethe graph is not a tree but for this test case it is
    
    // create a cluster for each variable
    Map<String, SumProductCluster> variableClusters = new HashMap<String, SumProductCluster>();
    for (DiscreteVariable variable : model.getScope().getVariables()) {
      SumProductCluster variableCluster = new SumProductCluster(new Scope(Arrays.asList(variable)));
      clusters.add(variableCluster);
      variableClusters.put(variable.getId(), variableCluster);
    }
    
    // create a cluster for each factor
    Set<SumProductCluster> factorClusters = new HashSet<SumProductCluster>();
    for (DiscreteFactor factor : model.getFactors()) {
      SumProductCluster factorCluster = new SumProductCluster(Sets.newLinkedHashSet(factor));
      clusters.add(factorCluster);
      factorClusters.add(factorCluster);
    }
    
    clusterGraph = new SumProductClusterGraph(clusters);
    
    for (SumProductCluster factorCluster : factorClusters) {
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
    return new CliqueTreeInferencer<SumProductCluster, SumProductMessage, SumProductEdge>(clusterGraph, rootCluster);
  }

}
