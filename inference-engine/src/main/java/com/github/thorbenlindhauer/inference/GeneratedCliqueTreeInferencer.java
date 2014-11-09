package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.graph.operation.ClusterGraphGenerator;
import com.github.thorbenlindhauer.network.GraphicalModel;

/**
 * Generates a cluster graph from the provided graphical model. Chooses root cluster randomly.
 * 
 * @author Thorben
 */
public class GeneratedCliqueTreeInferencer extends CliqueTreeInferencer {
  
  public GeneratedCliqueTreeInferencer(GraphicalModel graphicalModel, ClusterGraphGenerator clusterGraphGenerator, MessagePassingContextFactory messageContextFactory) {
    super(clusterGraphGenerator.generateClusterGraph(graphicalModel), null, messageContextFactory);
    
    // choose random root cluster
    rootCluster = clusterGraph.getClusters().iterator().next();
  }
}
