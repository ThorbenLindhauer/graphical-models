package com.github.thorbenlindhauer.network;

public interface ScopeBuilder {

  ScopeBuilder variable(String id, int cardinality);
  
  ModelBuilder done();
}
