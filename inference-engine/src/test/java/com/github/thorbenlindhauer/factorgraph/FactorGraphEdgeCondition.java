package com.github.thorbenlindhauer.factorgraph;

import org.assertj.core.api.Condition;

public class FactorGraphEdgeCondition extends Condition<FactorGraphEdge> {

  protected FactorGraphNode node1;
  protected FactorGraphNode node2;
  
  public FactorGraphEdgeCondition(FactorGraphNode node1, FactorGraphNode node2) {
    this.node1 = node1;
    this.node2 = node2;
  }
  
  @Override
  public boolean matches(FactorGraphEdge edge) {
    return edge.connects(node1) && edge.connects(node2);
  }
}
