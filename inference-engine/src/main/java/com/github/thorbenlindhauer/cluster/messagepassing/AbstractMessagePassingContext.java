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
import com.github.thorbenlindhauer.factor.Factor;

public abstract class AbstractMessagePassingContext<T extends Factor<T>> implements MessagePassingContext<T> {

  protected Map<Edge<T>, EdgeContext<T>> messages;

  protected Map<Cluster<T>, T> clusterPotentials;

  public AbstractMessagePassingContext(ClusterGraph<T> clusterGraph) {
    this.messages = new HashMap<Edge<T>, EdgeContext<T>>();

    for (Edge<T> edge : clusterGraph.getEdges()) {
      this.messages.put(edge, new EdgeContext<T>(edge, this));
    }

    this.clusterPotentials = new HashMap<Cluster<T>, T>();
  }

  @Override
  public T getClusterPotential(Cluster<T> cluster) {
    ensurePotentialInitialized(cluster);
    return clusterPotentials.get(cluster);
  }

  protected void ensurePotentialInitialized(Cluster<T> cluster) {
    T potential = clusterPotentials.get(cluster);

    if (potential == null) {
      potential = calculateClusterPotential(cluster);

      clusterPotentials.put(cluster, potential);
    }
  }

  protected abstract T calculateClusterPotential(Cluster<T> cluster);

  @Override
  public Message<T> getMessage(Edge<T> edge, Cluster<T> sourceCluster) {
    EdgeContext<T> edgeContext = messages.get(edge);

    if (edgeContext == null) {
      throw new InferenceException("Edge " + edge + " is not known to this context");
    }

    return edgeContext.getMessageFrom(sourceCluster);
  }

  protected abstract Message<T> newMessage(Cluster<T> sourceCluster, Edge<T> edge);

  protected static class EdgeContext<T extends Factor<T>> {
    protected Cluster<T> cluster1;
    protected Message<T> message1;

    protected Cluster<T> cluster2;
    protected Message<T> message2;

    public EdgeContext(Edge<T> edge, AbstractMessagePassingContext<T> messageFactory) {
      this.cluster1 = edge.getCluster1();
      this.message1 = messageFactory.newMessage(cluster1, edge);

      this.cluster2 = edge.getCluster2();
      this.message2 = messageFactory.newMessage(cluster2, edge);
    }

    public Message<T> getMessageFrom(Cluster<T> cluster) {
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
  public void notify(String eventName, Message<T> message) {
    if (MessageListener.UPDATE_EVENT.equals(eventName)) {
      // invalidate cached target cluster potential
      clusterPotentials.put(message.getTargetCluster(), null);
    }
  }
}
