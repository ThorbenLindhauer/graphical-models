/* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.thorbenlindhauer.cluster;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.ep.ClusterPotentialResolver;
import com.github.thorbenlindhauer.cluster.ep.DefaultPotentialResolver;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class Cluster<T extends Factor<T>> {

  protected Scope scope;
  protected Set<Edge<T>> edges;
  protected Set<T> factors;

  protected Cluster() {
    this.edges = new HashSet<Edge<T>>();
  }

  public Cluster(Scope scope, Set<T> factors) {
    this();

    // initialize scope from factors
    if (scope == null) {
      this.scope = new Scope(Collections.<DiscreteVariable>emptyList());
      for (T factor : factors) {
        this.scope = this.scope.union(factor.getVariables());
      }
    } else {
      // TODO: check if scope is superset of the factors' scopes
      this.scope = scope;
    }

    this.factors = factors;
  }

  public Cluster(Set<T> factors) {
    this(null, factors);
  }


  public Set<Edge<T>> getOtherEdges(Edge<T> outEdge) {
    // TODO: cache this?
    Set<Edge<T>> inEdges = new HashSet<Edge<T>>(edges);
    boolean contained = inEdges.remove(outEdge);

    if (!contained) {
      throw new ModelStructureException("Out edge " + outEdge + " is not connected to this cluster");
    }

    return inEdges;
  }

  public Edge<T> connectTo(Cluster<T> other) {
    Edge<T> newEdge = new Edge<T>(this, other);
    this.edges.add(newEdge);
    other.edges.add(newEdge);
    return newEdge;
  }

  public Scope getScope() {
    return scope;
  }

  public Set<Edge<T>> getEdges() {
    return edges;
  }

  public Set<T> getFactors() {
    return factors;
  }

  public ClusterPotentialResolver<T> getResolver() {
    return new DefaultPotentialResolver<T>(this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Cluster[Scope = ");
    sb.append(scope.toString());
    sb.append("]");
    return sb.toString();
  }
}
