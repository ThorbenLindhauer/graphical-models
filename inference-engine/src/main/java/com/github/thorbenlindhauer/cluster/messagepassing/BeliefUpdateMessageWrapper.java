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
import com.github.thorbenlindhauer.factor.DiscreteFactor;


/**
 * A view on {@link BeliefUpdateMessage} that provides a target cluster
 * 
 * @author Thorben
 */
public class BeliefUpdateMessageWrapper implements Message {

  protected BeliefUpdateMessage message;
  protected Cluster targetCluster;
  
  public BeliefUpdateMessageWrapper(BeliefUpdateMessage message, Cluster targetCluster) {
    this.message = message;
    this.targetCluster = targetCluster;
  }
  
  @Override
  public Edge getEdge() {
    return message.getEdge();
  }

  @Override
  public Cluster getTargetCluster() {
    return targetCluster;
  }

  @Override
  public Cluster getSourceCluster() {
    return getEdge().getTarget(targetCluster);
  }

  @Override
  public void update(MessagePassingContext messagePassingContext) {
    message.update(messagePassingContext, getSourceCluster(), targetCluster);
  }

  @Override
  public DiscreteFactor getPotential() {
    return message.getPotential();
  }
}
