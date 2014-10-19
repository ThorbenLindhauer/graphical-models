package com.github.thorbenlindhauer.cluster;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class Cluster {

  protected Scope scope;
  protected Set<DiscreteFactor> factors;
  protected DiscreteFactor jointFactor;
  protected DiscreteFactor potential;
  protected Set<Edge> edges;
  
  public Cluster(Scope scope, Set<DiscreteFactor> factors) {
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
    this.edges = new HashSet<Edge>();
  }
  
  public Cluster(Scope scope) {
    this(scope, new HashSet<DiscreteFactor>());
  }
  
  public Cluster(Set<DiscreteFactor> factors) {
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
  
  /**
   * Incoming messages * joint factor
   */
  public DiscreteFactor getPotential() {
    if (potential == null) {
      potential = getJointFactor();
      
      for (Edge edge : edges) {
        Message inMessage = edge.getMessageTo(this);
        DiscreteFactor messagePotential = inMessage.getPotential();
        
        // ignore null potentials
        if (potential == null) {
          potential = messagePotential;
        } else if (messagePotential != null) {
          potential = potential.product(messagePotential);
        }
      }
    }
    
    return potential;
  }
  
  public Scope getScope() {
    return scope;
  }
  
  public Edge connectTo(Cluster other) {
    Edge newEdge = new Edge(this, other);
    this.edges.add(newEdge);
    other.edges.add(newEdge);
    return newEdge;
  }
  
  // TODO: rename this, not necessarily "inEdges" but simply "all other edges"
  public Set<Edge> getInEdges(Edge outEdge) {
    // TODO: cache this?
    Set<Edge> inEdges = new HashSet<Edge>(edges);
    boolean contained = inEdges.remove(outEdge);
    
    if (!contained) {
      throw new ModelStructureException("Out edge " + outEdge + " is not connected to this cluster");
    }
    
    return inEdges;
  }
  
  public Set<Edge> getEdges() {
    return edges;
  }
}
