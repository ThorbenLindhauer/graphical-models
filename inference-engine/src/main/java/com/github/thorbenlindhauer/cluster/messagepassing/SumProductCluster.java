package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class SumProductCluster extends AbstractCluster<SumProductCluster, SumProductMessage, SumProductEdge> {

  protected Set<DiscreteFactor> factors;
  protected DiscreteFactor jointFactor;
  
  public SumProductCluster(Scope scope, Set<DiscreteFactor> factors) {
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
  
  public SumProductCluster(Scope scope) {
    this(scope, new HashSet<DiscreteFactor>());
  }
  
  public SumProductCluster(Set<DiscreteFactor> factors) {
    this(null, factors);
  }
  
  public Set<DiscreteFactor> getFactors() {
    return factors;
  }
  
  /**
   * Factor product of all the assigned factors
   */
  public DiscreteFactor getJointFactor() {
    if (jointFactor == null) {
      jointFactor = FactorUtil.jointDistribution(factors);
    }
    
    return jointFactor;
  }
  
  @Override
  protected void ensurePotentialInitialized() {
    if (potential == null) {
      potential = getJointFactor();
      
      for (SumProductEdge edge : edges) {
        SumProductMessage inMessage = edge.getMessageTo(this);
        DiscreteFactor messagePotential = inMessage.getPotential();
        
        // ignore null potentials
        if (potential == null) {
          potential = messagePotential;
        } else if (messagePotential != null) {
          potential = potential.product(messagePotential);
        }
      }
    }
  }
  
  public SumProductEdge connectTo(SumProductCluster other) {
    SumProductEdge newEdge = new SumProductEdge(this, other);
    this.edges.add(newEdge);
    other.edges.add(newEdge);
    return newEdge;
  }
  
}
