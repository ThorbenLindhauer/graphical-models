package com.github.thorbenlindhauer.graph.operation;

import java.util.HashSet;
import java.util.Set;

public class UnionFindPartition<T> {

  protected Set<T> elements;
  
  public UnionFindPartition() {
    this.elements = new HashSet<T>();
  }
  
  public void add(T element) {
    this.elements.add(element);
  }
  
  public Set<T> getElements() {
    return elements;
  }
  
  public void union(UnionFindPartition<T> other) {
    this.elements.addAll(other.elements);
  }
}
