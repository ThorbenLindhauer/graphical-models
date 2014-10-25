package com.github.thorbenlindhauer.cluster.messagepassing;


public interface Message<R extends MessagePassingCluster<R, S, T>, 
S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> {

  void update();
  
  T getEdge();
  
  R getTargetCluster();
  
  R getSourceCluster();
}
