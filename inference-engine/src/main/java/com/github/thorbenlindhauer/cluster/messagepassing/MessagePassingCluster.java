package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

public interface MessagePassingCluster<R extends MessagePassingCluster<R, S, T>, 
S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> {

  Scope getScope();
  
  DiscreteFactor getPotential();
  
  Set<T> getOtherEdges(T outEdge);
  
  Set<T> getEdges();
}
