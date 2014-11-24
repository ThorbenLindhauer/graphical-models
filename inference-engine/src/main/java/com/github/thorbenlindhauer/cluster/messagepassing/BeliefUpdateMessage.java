package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

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
public class BeliefUpdateMessage {

  protected DiscreteFactor potential;
  protected Edge edge;

  public BeliefUpdateMessage(Edge edge) {
    this.edge = edge;
  }
  
  public void update(MessagePassingContext messagePassingContext, Cluster sourceCluster, Cluster targetCluster) {
    DiscreteFactor newPotential = messagePassingContext.getClusterPotential(sourceCluster).marginal(edge.getScope());
    
    DiscreteFactor targetUpdate = newPotential;
    if (potential != null) {
      targetUpdate = newPotential.division(potential);
    }

    DiscreteFactor newTargetPotential = targetUpdate;
    
    DiscreteFactor targetPotential = messagePassingContext.getClusterPotential(targetCluster);
    if (targetPotential != null) {
      newTargetPotential = newTargetPotential.product(targetPotential);
    }
    
    messagePassingContext.updateClusterPotential(targetCluster, newTargetPotential);
    
    potential = newPotential;
  }

  public BeliefUpdateMessageWrapper wrapAsDirectedMessage(Cluster targetCluster) {
    return new BeliefUpdateMessageWrapper(this, targetCluster);
  }
  
  public Edge getEdge() {
    return edge;
  }
  
  public DiscreteFactor getPotential() {
    return potential;
  }
}
