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
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.FactorSet;

/**
 * A message is assigned to an edge and its direction is specified by the source cluster
 *
 * @author Thorben
 */
public abstract class AbstractMessage implements Message {

  protected Cluster sourceCluster;
  protected FactorSet potential;

  protected Edge edge;

  public AbstractMessage(Cluster cluster, Edge edge) {
    this.edge = edge;
    if (!edge.connects(cluster)) {
      throw new ModelStructureException("Invalid message: Cluster " + cluster + " is not involved in edge " + edge);
    }

    this.sourceCluster = cluster;
  }

  @Override
  public abstract void update(MessagePassingContext messagePassingContext);

  @Override
  public FactorSet getPotential() {
    return potential;
  }

  @Override
  public Cluster getTargetCluster() {
    return edge.getTarget(sourceCluster);
  }

  @Override
  public Cluster getSourceCluster() {
    return sourceCluster;
  }

  @Override
  public Edge getEdge() {
    return edge;
  }
}
