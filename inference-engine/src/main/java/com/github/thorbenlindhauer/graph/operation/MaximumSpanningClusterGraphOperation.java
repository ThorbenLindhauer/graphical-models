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

public class MaximumSpanningClusterGraphOperation {

  public ClusterGraph execute(Set<Cluster> clusters) {
    ClusterGraph clusterGraph = new ClusterGraph(clusters);
    
    List<EdgeCandidate> edgeCandidates = initializeEdgeCandidates(clusters);
    Collections.sort(edgeCandidates, new EdgeCandidateWeightComparator());
    UnionFindStructure<Cluster> partitionedClusters = new UnionFindStructure<Cluster>(clusters);
    
    boolean edgeAdded = true;
    while (partitionedClusters.getNumPartitions() > 1) {
      if (!edgeAdded) {
        throw new ModelStructureException("No edge added although there are still unconnected clusters");
      }
      edgeAdded = false;
      
      Iterator<EdgeCandidate> edgeCandidateIt = edgeCandidates.iterator();
      
      while (edgeCandidateIt.hasNext()) {
        EdgeCandidate candidate = edgeCandidateIt.next();
        
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
  
  protected List<EdgeCandidate> initializeEdgeCandidates(Set<Cluster> clusters) {
    List<Cluster> clusterList = new ArrayList<Cluster>(clusters);
    List<EdgeCandidate> edgeCandidates = new LinkedList<EdgeCandidate>();
    
    for (int i = 0; i < clusterList.size(); i++) {
      Cluster cluster1 = clusterList.get(i);
      for (int j = i + 1; j < clusterList.size(); j++) {
        Cluster cluster2 = clusterList.get(j);
        if (cluster1.getScope().intersect(cluster2.getScope()).size() > 0) {
          edgeCandidates.add(new EdgeCandidate(cluster1, cluster2));
        }
      }
    }
    
    return edgeCandidates;
  }
  
  protected static class EdgeCandidate {
    
    protected Cluster cluster1;
    protected Cluster cluster2;
    protected int weight;
    
    public EdgeCandidate(Cluster cluster1, Cluster cluster2) {
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
  protected static class EdgeCandidateWeightComparator implements Comparator<EdgeCandidate> {

    @Override
    public int compare(EdgeCandidate o1, EdgeCandidate o2) {
      return o2.weight - o1.weight;
    }
    
  }
}
