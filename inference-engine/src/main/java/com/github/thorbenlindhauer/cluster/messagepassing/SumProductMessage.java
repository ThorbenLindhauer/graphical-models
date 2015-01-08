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

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.factor.FactorSet;

/**
 * A message is assigned to an edge and its direction is specified by the source cluster
 *
 * @author Thorben
 */
public class SumProductMessage<T extends Factor<T>> extends AbstractMessage<T> {

  public SumProductMessage(Cluster<T> cluster, Edge<T> edge) {
    super(cluster, edge);
  }

  @Override
  public void update(MessagePassingContext<T> messagePassingContext) {
    Set<Message<T>> inMessages = new HashSet<Message<T>>();
    Set<Edge<T>> inEdges = sourceCluster.getOtherEdges(edge);

    for (Edge<T> inEdge : inEdges) {
      inMessages.add(messagePassingContext.getMessage(inEdge, inEdge.getTarget(sourceCluster)));
    }

    FactorSet<T> inMessagePotentials = new FactorSet<T>();

    for (Message<T> inMessage : inMessages) {
      FactorSet<T> inMessagePotential = inMessage.getPotential();
      if (inMessagePotential != null) {
        inMessagePotentials.product(inMessagePotential);
      }
    }

    potential = sourceCluster.getResolver().project(inMessagePotentials, edge.getScope());
  }
}
