package com.github.thorbenlindhauer.cluster;

import org.assertj.core.api.Condition;

import com.github.thorbenlindhauer.factor.DiscreteFactor;

public class ClusterFactorCondition extends Condition<Cluster> {

  protected DiscreteFactor factor;
  
  public ClusterFactorCondition(DiscreteFactor factor) {
    this.factor = factor;
  }
  
  @Override
  public boolean matches(Cluster cluster) {
    return cluster.getFactors().contains(factor) && cluster.getScope().contains(factor.getVariables());
  }
}
