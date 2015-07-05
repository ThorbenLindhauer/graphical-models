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

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.factor.FactorSet;

public class BeliefUpdateMessage<T extends Factor<T>> extends AbstractMessage<T> {

  public BeliefUpdateMessage(Cluster<T> cluster, Edge<T> edge) {
    super(cluster, edge);
  }

  @Override
  public void update(MessagePassingContext<T> messagePassingContext) {
    Cluster<T> targetCluster = edge.getTarget(sourceCluster);

    FactorSet<T> messagesForSourceCluster = messagePassingContext.getClusterMessages(sourceCluster);
    FactorSet<T> sourceClusterPotentialProjection =
        sourceCluster.getResolver().project(messagesForSourceCluster, edge.getScope());

    FactorSet<T> messagesForTargetCluster = messagePassingContext.getClusterMessages(targetCluster);
    if (potential != null) {
      messagesForTargetCluster.division(potential);
    }

    potential = sourceClusterPotentialProjection;

    FactorSet<T> reverseMessage = messagePassingContext.getMessage(edge, targetCluster).getPotential();
    if (reverseMessage != null) {
      potential.division(reverseMessage);
    }

    messagesForTargetCluster.product(potential);
  }

}
