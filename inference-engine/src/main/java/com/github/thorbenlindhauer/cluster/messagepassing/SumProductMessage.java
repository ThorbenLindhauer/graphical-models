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
import com.github.thorbenlindhauer.factor.FactorSet;

/**
 * A message is assigned to an edge and its direction is specified by the source cluster
 *
 * @author Thorben
 */
public class SumProductMessage extends AbstractMessage {

  public SumProductMessage(Cluster cluster, Edge edge) {
    super(cluster, edge);
  }

  @Override
  public void update(MessagePassingContext messagePassingContext) {
    Set<Message> inMessages = new HashSet<Message>();
    Set<Edge> inEdges = sourceCluster.getOtherEdges(edge);

    for (Edge inEdge : inEdges) {
      inMessages.add(messagePassingContext.getMessage(inEdge, inEdge.getTarget(sourceCluster)));
    }

    FactorSet inMessagePotentials = new FactorSet();

    for (Message inMessage : inMessages) {
      FactorSet inMessagePotential = inMessage.getPotential();
      if (inMessagePotential != null) {
        inMessagePotentials.product(inMessagePotential);
      }
    }

    potential = sourceCluster.getResolver().project(inMessagePotentials, edge.getScope());
  }
}
