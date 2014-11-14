package com.github.thorbenlindhauer.inference;

import com.github.thorbenlindhauer.cluster.generation.CliqueTreeGenerator;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.network.GraphicalModel;

/**
 * Generates a cluster graph from the provided graphical model. Chooses root cluster randomly.
 * 
 * @author Thorben
 */
public class GeneratedCliqueTreeInferencer extends CliqueTreeInferencer {
  
  public GeneratedCliqueTreeInferencer(GraphicalModel graphicalModel, CliqueTreeGenerator clusterGraphGenerator, MessagePassingContextFactory messageContextFactory) {
    super(clusterGraphGenerator.generateClusterGraph(graphicalModel), null, messageContextFactory);
    
    // choose random root cluster
    rootCluster = clusterGraph.getClusters().iterator().next();
  }
}
