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
package com.github.thorbenlindhauer.graph.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.Factor;

public class MaximumSpanningClusterGraphOperation {

  public <T extends Factor<T>> ClusterGraph<T> execute(Set<Cluster<T>> clusters) {
    ClusterGraph<T> clusterGraph = new ClusterGraph<T>(clusters);

    List<EdgeCandidate<T>> edgeCandidates = initializeEdgeCandidates(clusters);
    Collections.sort(edgeCandidates, new EdgeCandidateWeightComparator());
    UnionFindStructure<Cluster<T>> partitionedClusters = new UnionFindStructure<Cluster<T>>(clusters);

    boolean edgeAdded = true;
    while (partitionedClusters.getNumPartitions() > 1) {
      if (!edgeAdded) {
        throw new ModelStructureException("No edge added although there are still unconnected clusters");
      }
      edgeAdded = false;

      Iterator<EdgeCandidate<T>> edgeCandidateIt = edgeCandidates.iterator();

      while (edgeCandidateIt.hasNext()) {
        EdgeCandidate<T> candidate = edgeCandidateIt.next();

        if (partitionedClusters.getPartition(candidate.cluster1) != partitionedClusters.getPartition(candidate.cluster2)) {
          clusterGraph.connect(candidate.cluster1, candidate.cluster2);
          partitionedClusters.union(candidate.cluster1, candidate.cluster2);
          edgeCandidateIt.remove();
          edgeAdded = true;
          break;
        }
      }
    }

    return clusterGraph;
  }

  protected <T extends Factor<T>> List<EdgeCandidate<T>> initializeEdgeCandidates(Set<Cluster<T>> clusters) {
    List<Cluster<T>> clusterList = new ArrayList<Cluster<T>>(clusters);
    List<EdgeCandidate<T>> edgeCandidates = new LinkedList<EdgeCandidate<T>>();

    for (int i = 0; i < clusterList.size(); i++) {
      Cluster<T> cluster1 = clusterList.get(i);
      for (int j = i + 1; j < clusterList.size(); j++) {
        Cluster<T> cluster2 = clusterList.get(j);
        if (cluster1.getScope().intersect(cluster2.getScope()).size() > 0) {
          edgeCandidates.add(new EdgeCandidate<T>(cluster1, cluster2));
        }
      }
    }

    return edgeCandidates;
  }

  protected static class EdgeCandidate<T extends Factor<T>> {

    protected Cluster<T> cluster1;
    protected Cluster<T> cluster2;
    protected int weight;

    public EdgeCandidate(Cluster<T> cluster1, Cluster<T> cluster2) {
      this.cluster1 = cluster1;
      this.cluster2 = cluster2;
      this.weight = cluster1.getScope().intersect(cluster2.getScope()).size();
    }
  }

  /**
   * Desceding edge weight comparator
   * @author Thorben
   *
   */
  protected static class EdgeCandidateWeightComparator implements Comparator<EdgeCandidate<?>> {

    @Override
    public int compare(EdgeCandidate<?> o1, EdgeCandidate<?> o2) {
      return o2.weight - o1.weight;
    }

  }
}
