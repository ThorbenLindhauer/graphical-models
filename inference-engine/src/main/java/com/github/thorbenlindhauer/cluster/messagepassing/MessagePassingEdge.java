package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.variable.Scope;

public interface MessagePassingEdge<R extends MessagePassingCluster<R, S, T>, 
S extends Message<R, S, T>, T extends MessagePassingEdge<R, S, T>> {

  S getMessageFrom(R cluster);
  
  S getMessageTo(R cluster);
  
  Scope getScope();
}
