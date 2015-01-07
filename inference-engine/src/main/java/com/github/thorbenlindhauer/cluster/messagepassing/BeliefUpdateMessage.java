package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.FactorSet;

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
public class BeliefUpdateMessage extends AbstractMessage {

  public BeliefUpdateMessage(Cluster cluster, Edge edge) {
    super(cluster, edge);
  }

  @Override
  public void update(MessagePassingContext messagePassingContext) {
    Cluster targetCluster = edge.getTarget(sourceCluster);

    FactorSet messagesForSourceCluster = messagePassingContext.getClusterMessages(sourceCluster);
    FactorSet sourceClusterPotentialProjection =
        sourceCluster.getResolver().project(messagesForSourceCluster, edge.getScope());

    FactorSet messagesForTargetCluster = messagePassingContext.getClusterMessages(targetCluster);
    if (potential != null) {
      messagesForTargetCluster.division(potential);
    }

    potential = sourceClusterPotentialProjection;

    FactorSet reverseMessage = messagePassingContext.getMessage(edge, targetCluster).getPotential();
    if (reverseMessage != null) {
      potential.division(reverseMessage);
    }

    messagesForTargetCluster.product(potential);
  }

}
