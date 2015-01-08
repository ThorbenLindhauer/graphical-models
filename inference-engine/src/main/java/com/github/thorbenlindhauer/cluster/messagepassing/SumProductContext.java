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
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.factor.FactorSet;

public class SumProductContext<T extends Factor<T>> extends AbstractMessagePassingContext<T> {


  public SumProductContext(ClusterGraph<T> clusterGraph) {
    super(clusterGraph);
  }

  protected T calculateClusterPotential(Cluster<T> cluster) {
    FactorSet<T> inMessageFactors = new FactorSet<T>();

    for (Edge<T> edge : cluster.getEdges()) {
      Message<T> inMessage = getMessage(edge, edge.getTarget(cluster));
      FactorSet<T> messagePotential = inMessage.getPotential();

      // ignore null potentials
      if (messagePotential != null) {
        inMessageFactors.product(messagePotential);
      }
    }

    FactorSet<T> potentialFactors = cluster.getResolver().project(inMessageFactors, cluster.getScope());
    T potential = potentialFactors.toFactor();

    return potential;
  }

  @Override
  public FactorSet<T> getClusterMessages(Cluster<T> cluster) {
    throw new UnsupportedOperationException("not implemented; not required for sum product algorithm");
  }

  @Override
  protected Message<T> newMessage(Cluster<T> sourceCluster, Edge<T> edge) {
    return new SumProductMessage<T>(sourceCluster, edge);
  }

}
