package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.Set;

public interface MessagePassingClusterGraph<R extends MessagePassingCluster<R, S, T>, 
  S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> {

  Set<R> getClusters();
}
