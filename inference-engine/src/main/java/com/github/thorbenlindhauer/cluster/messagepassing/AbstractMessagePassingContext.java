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
import com.github.thorbenlindhauer.exception.InferenceException;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

public abstract class AbstractMessagePassingContext implements MessagePassingContext {

  protected Map<Edge, EdgeContext> messages;

  protected Map<Cluster, DiscreteFactor> clusterPotentials;

  public AbstractMessagePassingContext(ClusterGraph clusterGraph) {
    this.messages = new HashMap<Edge, EdgeContext>();

    for (Edge edge : clusterGraph.getEdges()) {
      this.messages.put(edge, new EdgeContext(edge, this));
    }

    this.clusterPotentials = new HashMap<Cluster, DiscreteFactor>();
  }

  @Override
  public DiscreteFactor getClusterPotential(Cluster cluster) {
    ensurePotentialInitialized(cluster);
    return clusterPotentials.get(cluster);
  }

  protected void ensurePotentialInitialized(Cluster cluster) {
    DiscreteFactor potential = clusterPotentials.get(cluster);

    if (potential == null) {
      potential = calculateClusterPotential(cluster);

      clusterPotentials.put(cluster, potential);
    }
  }

  protected abstract DiscreteFactor calculateClusterPotential(Cluster cluster);

  @Override
  public Message getMessage(Edge edge, Cluster sourceCluster) {
    EdgeContext edgeContext = messages.get(edge);

    if (edgeContext == null) {
      throw new InferenceException("Edge " + edge + " is not known to this context");
    }

    return edgeContext.getMessageFrom(sourceCluster);
  }

  protected abstract Message newMessage(Cluster sourceCluster, Edge edge);

  protected static class EdgeContext {
    protected Cluster cluster1;
    protected Message message1;

    protected Cluster cluster2;
    protected Message message2;

    public EdgeContext(Edge edge, AbstractMessagePassingContext messageFactory) {
      this.cluster1 = edge.getCluster1();
      this.message1 = messageFactory.newMessage(cluster1, edge);

      this.cluster2 = edge.getCluster2();
      this.message2 = messageFactory.newMessage(cluster2, edge);
    }

    public Message getMessageFrom(Cluster cluster) {
      if (cluster == cluster1) {
        return message1;
      } else if (cluster == cluster2) {
        return message2;
      } else {
        throw new ModelStructureException("");
      }
    }
  }

  @Override
  public void notify(String eventName, Message message) {
    if (MessageListener.UPDATE_EVENT.equals(eventName)) {
      // invalidate cached target cluster potential
      clusterPotentials.put(message.getTargetCluster(), null);
    }
  }
}
