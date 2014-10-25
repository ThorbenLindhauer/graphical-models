package com.github.thorbenlindhauer.inference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;

import com.github.thorbenlindhauer.cluster.messagepassing.BeliefPropagationCluster;
import com.github.thorbenlindhauer.cluster.messagepassing.BeliefPropagationClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.BeliefPropagationEdge;
import com.github.thorbenlindhauer.cluster.messagepassing.BeliefPropagationMessageWrapper;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class BeliefPropagationCliqueTreeInferencerTest extends ExactInferencerTest {

  protected BeliefPropagationClusterGraph clusterGraph;
  protected BeliefPropagationCluster rootCluster;
  
  @Before
  public void setUpClusterGraph() {
    Set<BeliefPropagationCluster> clusters = new HashSet<BeliefPropagationCluster>();

    // constructs a bethe cluster graph
    // in general, a bethe graph is not a tree but for this test case it is
    
    // create a cluster for each variable
    Map<String, BeliefPropagationCluster> variableClusters = new HashMap<String, BeliefPropagationCluster>();
    for (DiscreteVariable variable : model.getScope().getVariables()) {
      BeliefPropagationCluster variableCluster = new BeliefPropagationCluster(new Scope(Arrays.asList(variable)));
      clusters.add(variableCluster);
      variableClusters.put(variable.getId(), variableCluster);
    }
    
    // create a cluster for each factor
    Set<BeliefPropagationCluster> factorClusters = new HashSet<BeliefPropagationCluster>();
    for (DiscreteFactor factor : model.getFactors()) {
      BeliefPropagationCluster factorCluster = new BeliefPropagationCluster(Sets.newLinkedHashSet(factor));
      clusters.add(factorCluster);
      factorClusters.add(factorCluster);
    }
    
    clusterGraph = new BeliefPropagationClusterGraph(clusters);
    
    for (BeliefPropagationCluster factorCluster : factorClusters) {
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
    return new CliqueTreeInferencer<BeliefPropagationCluster, BeliefPropagationMessageWrapper, BeliefPropagationEdge>(clusterGraph, rootCluster);
  }

}
