package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

public abstract class AbstractCluster<R extends MessagePassingCluster<R, S, T>, 
S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> implements MessagePassingCluster<R, S, T> {

  protected Scope scope;
  protected DiscreteFactor potential;
  protected Set<T> edges;
  
  public AbstractCluster(Scope scope) {
    this.scope = scope;
    this.edges = new HashSet<T>();
  }
  
  @Override
  public DiscreteFactor getPotential() {
    ensurePotentialInitialized();
    return potential;
  }
  
  @Override
  public Scope getScope() {
    return scope;
  }
  
  @Override
  public Set<T> getEdges() {
    return edges;
  }
  
  @Override
  public Set<T> getOtherEdges(T outEdge) {
    // TODO: cache this?
    Set<T> inEdges = new HashSet<T>(edges);
    boolean contained = inEdges.remove(outEdge);
    
    if (!contained) {
      throw new ModelStructureException("Out edge " + outEdge + " is not connected to this cluster");
    }
    
    return inEdges;
  }
  
  protected abstract void ensurePotentialInitialized();
}
