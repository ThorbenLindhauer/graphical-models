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

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.variable.Scope;

// TODO: ClusterGraph should be immutable as otherwise one might think it can be modified during inference
// regards methods like addCluster and the scope constructor
public class ClusterGraph<T extends Factor<T>> {

  protected Set<Cluster<T>> clusters;
  protected Set<Edge<T>> edges;
  protected Scope scope;

  public ClusterGraph(Scope scope) {
    this(new HashSet<Cluster<T>>());
    this.scope = scope;
  }

  public ClusterGraph(Set<Cluster<T>> clusters) {
    this.clusters = clusters;
    this.edges = new HashSet<Edge<T>>();
  }

  public Set<Cluster<T>> getClusters() {
    return clusters;
  }

  public Set<Edge<T>> getEdges() {
    return edges;
  }

  public void addCluster(Cluster<T> cluster) {
    initScope();
    if (!scope.contains(cluster.getScope())) {
      throw new ModelStructureException("Cluster scope " + cluster.getScope() + " is not part of this graph's scope " + scope);
    }

    this.clusters.add(cluster);
  }

  public Edge<T> connect(Cluster<T> cluster1, Cluster<T> cluster2) {
    if (!clusters.contains(cluster1) || !clusters.contains(cluster2)) {
      throw new ModelStructureException("At least one of the cluster " + cluster1 + ", "
          + cluster2 + " is not contained by this graph.");
    }

    Edge<T> newEdge = cluster1.connectTo(cluster2);
    this.edges.add(newEdge);
    return newEdge;
  }

  public void initScope() {
    if (scope == null) {
      for (Cluster<T> cluster : clusters) {
        if (scope == null) {
          scope = cluster.getScope();
        } else {
          scope = scope.union(cluster.getScope());
        }
      }
    }
  }

  public Scope getScope() {
    if (scope == null) {
      initScope();
    }

    return scope;
  }
}
