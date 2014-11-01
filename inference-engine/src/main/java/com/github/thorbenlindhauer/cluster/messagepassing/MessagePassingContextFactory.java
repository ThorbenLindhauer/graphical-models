package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.ClusterGraph;

public interface MessagePassingContextFactory {

  MessagePassingContext newMessagePassingContext(ClusterGraph clusterGraph);
}
