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
import com.github.thorbenlindhauer.factor.FactorUtil;

public class BeliefUpdateContext implements MessagePassingContext {

  protected Map<Edge, BeliefUpdateMessage> messages;
  protected Map<Cluster, DiscreteFactor> clusterPotentials;

  public BeliefUpdateContext(ClusterGraph clusterGraph) {
    initializeClusterPotentials(clusterGraph);
    initializeMessages(clusterGraph);
  }

  protected void initializeMessages(ClusterGraph clusterGraph) {
    this.messages = new HashMap<Edge, BeliefUpdateMessage>();
    for (Edge edge : clusterGraph.getEdges()) {
      BeliefUpdateMessage message = new BeliefUpdateMessage(edge);
      messages.put(edge, message);
    }

  }

  protected void initializeClusterPotentials(ClusterGraph clusterGraph) {
    clusterPotentials = new HashMap<Cluster, DiscreteFactor>();

    for (Cluster cluster : clusterGraph.getClusters()) {
      clusterPotentials.put(cluster, FactorUtil.jointDistribution(cluster.getFactors()));
    }
  }

  @Override
  public Message getMessage(Edge edge, Cluster sourceCluster) {
    BeliefUpdateMessage message = messages.get(edge);
    BeliefUpdateMessageWrapper wrapper = message.wrapAsDirectedMessage(edge.getTarget(sourceCluster));

    return wrapper;
  }

  @Override
  public DiscreteFactor getJointDistribution(Cluster cluster) {
    throw new UnsupportedOperationException("This context does not cache joint distributions");
  }

  @Override
  public DiscreteFactor getClusterPotential(Cluster cluster) {
    return clusterPotentials.get(cluster);
  }

  @Override
  public void updateClusterPotential(Cluster cluster, DiscreteFactor factor) {
    clusterPotentials.put(cluster, factor);
  }

  @Override
  public void notify(String eventName, Message object) {
    // do nothing
  }
}
