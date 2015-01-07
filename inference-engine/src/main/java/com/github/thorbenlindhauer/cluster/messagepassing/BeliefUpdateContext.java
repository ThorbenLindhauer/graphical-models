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
package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashMap;
import java.util.Map;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorSet;

public class BeliefUpdateContext extends AbstractMessagePassingContext {

  protected Map<Cluster, FactorSet> clusterMessages;

  public BeliefUpdateContext(ClusterGraph clusterGraph) {
    super(clusterGraph);
    initializeClusterPotentials(clusterGraph);
  }

  protected void initializeClusterPotentials(ClusterGraph clusterGraph) {
    clusterMessages = new HashMap<Cluster, FactorSet>();

    for (Cluster cluster : clusterGraph.getClusters()) {
      clusterMessages.put(cluster, new FactorSet());
    }
  }

  @Override
  public DiscreteFactor calculateClusterPotential(Cluster cluster) {
    return cluster.getResolver().project(clusterMessages.get(cluster), cluster.getScope()).toFactor();
  }

  @Override
  public FactorSet getClusterMessages(Cluster cluster) {
    return clusterMessages.get(cluster);
  }

  @Override
  protected Message newMessage(Cluster sourceCluster, Edge edge) {
    return new BeliefUpdateMessage(sourceCluster, edge);
  }
}
