package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class BeliefPropagationCluster extends AbstractCluster<BeliefPropagationCluster, BeliefPropagationMessageWrapper, BeliefPropagationEdge> {

  // TODO: since the factors are not required for the algorithm, this member may be removed to save memory
  protected Set<DiscreteFactor> factors;
  
  public BeliefPropagationCluster(Scope scope, Set<DiscreteFactor> factors) {
    super(scope);
    
    // initialize scope from factors
    if (scope == null) {
      this.scope = new Scope(Collections.<DiscreteVariable>emptyList());
      for (DiscreteFactor factor : factors) {
        this.scope = this.scope.union(factor.getVariables());
      }
    } else {
      // TODO: check if scope is superset of the factors' scopes
      this.scope = scope;
    }
    
    this.factors = factors;
  }
  
  public BeliefPropagationCluster(Scope scope) {
    super(scope);
    
    double[] values = new double[scope.getNumDistinctValues()];
    Arrays.fill(values, 1);
    DiscreteFactor constantFactor = new TableBasedDiscreteFactor(scope, values);
    this.factors = new HashSet<DiscreteFactor>();
    factors.add(constantFactor);
  }
  
  public BeliefPropagationCluster(Set<DiscreteFactor> factors) {
    this(null, factors);
  }

  @Override
  protected void ensurePotentialInitialized() {
    if (potential == null) {
      potential = FactorUtil.jointDistribution(factors);
    }
  }
  
  public void updatePotential(DiscreteFactor multiplier) {
    ensurePotentialInitialized();
    
    potential = potential.product(multiplier);
  }
  
  public BeliefPropagationEdge connectTo(BeliefPropagationCluster other) {
    BeliefPropagationEdge newEdge = new BeliefPropagationEdge(this, other);
    this.edges.add(newEdge);
    other.edges.add(newEdge);
    return newEdge;
  }
}
