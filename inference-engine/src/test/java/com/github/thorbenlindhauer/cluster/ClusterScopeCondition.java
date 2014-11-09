package com.github.thorbenlindhauer.cluster;

import org.assertj.core.api.Condition;

public class ClusterScopeCondition extends Condition<Cluster> {

  protected String[] variableIds;
  
  public ClusterScopeCondition(String[] variableIds) {
    this.variableIds = variableIds;
  }
  
  @Override
  public boolean matches(Cluster value) {
    return value.getScope().contains(variableIds) && value.getScope().size() == variableIds.length;
  }
}
