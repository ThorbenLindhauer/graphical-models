package com.github.thorbenlindhauer.cluster;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class Cluster {
  
  protected Scope scope;
  protected Set<Edge> edges;
  protected Set<DiscreteFactor> factors;
  
  protected Cluster() {
    this.edges = new HashSet<Edge>();
  }

  public Cluster(Scope scope, Set<DiscreteFactor> factors) {
    this();
    
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
  
  public Cluster(Scope scope) {
    this();
    
    this.scope = scope;
    double[] values = new double[scope.getNumDistinctValues()];
    Arrays.fill(values, 1);
    DiscreteFactor constantFactor = new TableBasedDiscreteFactor(scope, values);
    this.factors = new HashSet<DiscreteFactor>();
    factors.add(constantFactor);
  }
  
  public Cluster(Set<DiscreteFactor> factors) {
    this(null, factors);
  }

  
  public Set<Edge> getOtherEdges(Edge outEdge) {
    // TODO: cache this?
    Set<Edge> inEdges = new HashSet<Edge>(edges);
    boolean contained = inEdges.remove(outEdge);
    
    if (!contained) {
      throw new ModelStructureException("Out edge " + outEdge + " is not connected to this cluster");
    }
    
    return inEdges;
  }
  
  public Edge connectTo(Cluster other) {
    Edge newEdge = new Edge(this, other);
    this.edges.add(newEdge);
    other.edges.add(newEdge);
    return newEdge;
  }
  
  public Scope getScope() {
    return scope;
  }
  
  public Set<Edge> getEdges() {
    return edges;
  }
  
  public Set<DiscreteFactor> getFactors() {
    return factors;
  }
}
