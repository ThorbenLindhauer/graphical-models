package com.github.thorbenlindhauer.cluster.messagepassing;

public abstract class AbstractMessage<R extends MessagePassingCluster<R, S, T>, 
  S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> implements Message<R, S, T> {

  protected T edge;
  
  public AbstractMessage(T edge) {
    this.edge = edge;
  }
  
  @Override
  public T getEdge() {
    return edge;
  }
}
