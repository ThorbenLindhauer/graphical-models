package com.github.thorbenlindhauer.graph.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.inference.variableelimination.MinFillEliminationStrategy;
import com.github.thorbenlindhauer.inference.variableelimination.VariableEliminationStrategy;
import com.github.thorbenlindhauer.network.GraphicalModel;

/**
 * Generates a cluster graph from a graphical model by the following strategy:
 * 
 * <ul>
 *   <li>determine a variable elimination order</li>
 *   <li>moralize graphical model</li>
 *   <li>determine induced (and triangulated) graph based on that order</li>
 *   <li>find maximum clique by maximum cardinality search</li>
 *   <li>determine edges by maximum spanning tree over the clusters and their sepsets</li>
 * </ul>
 * 
 * @author Thorben
 */
public class ClusterGraphGenerator {

  public ClusterGraph generateClusterGraph(GraphicalModel graphicalModel) {
    List<String> eliminationOrder = getEliminationStrategy().getEliminationOrder(graphicalModel, 
        Arrays.asList(graphicalModel.getScope().getVariableIds()));
    
    FactorGraph moralizedGraph = FactorGraph.fromGraphicalModel(graphicalModel.getFactors());
    
    FactorGraph inducedGraph = getTriangulator().getInducedGraph(moralizedGraph, eliminationOrder);
    
    Set<Cluster> clusters = getMaximumCliqueAnalyzer().execute(inducedGraph);
    
    ClusterGraph clusterGraph = getMaximumSpanningTreeAnalyzer().execute(clusters);
    
    return clusterGraph;
  }
  
  protected VariableEliminationStrategy getEliminationStrategy() {
    return new MinFillEliminationStrategy();
  }
  
  protected Triangulator getTriangulator() {
    return new Triangulator();
  }
  
  protected MaximumCardinalityCliqueOperation getMaximumCliqueAnalyzer() {
    return new MaximumCardinalityCliqueOperation();
  }
  
  protected MaximumSpanningClusterGraphOperation getMaximumSpanningTreeAnalyzer() {
    return new MaximumSpanningClusterGraphOperation();
  }
  
  
}
