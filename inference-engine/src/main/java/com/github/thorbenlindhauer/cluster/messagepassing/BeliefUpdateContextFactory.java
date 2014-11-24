package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.ClusterGraph;

public class BeliefUpdateContextFactory implements MessagePassingContextFactory {

  @Override
  public MessagePassingContext newMessagePassingContext(ClusterGraph clusterGraph) {
    return new BeliefUpdateContext(clusterGraph);
  }
}
