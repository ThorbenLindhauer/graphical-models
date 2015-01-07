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
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.FactorSet;

public class SumProductContext extends AbstractMessagePassingContext {


  public SumProductContext(ClusterGraph clusterGraph) {
    super(clusterGraph);
  }

  protected DiscreteFactor calculateClusterPotential(Cluster cluster) {
    FactorSet inMessageFactors = new FactorSet();

    for (Edge edge : cluster.getEdges()) {
      Message inMessage = getMessage(edge, edge.getTarget(cluster));
      FactorSet messagePotential = inMessage.getPotential();

      // ignore null potentials
      if (messagePotential != null) {
        inMessageFactors.product(messagePotential);
      }
    }

    FactorSet potentialFactors = cluster.getResolver().project(inMessageFactors, cluster.getScope());
    DiscreteFactor potential = potentialFactors.toFactor();

    return potential;
  }

  protected static class EdgeContext {
    protected Cluster cluster1;
    protected Message message1;

    protected Cluster cluster2;
    protected Message message2;

    public EdgeContext(Edge edge) {
      this.cluster1 = edge.getCluster1();
      this.message1 = new SumProductMessage(cluster1, edge);

      this.cluster2 = edge.getCluster2();
      this.message2 = new SumProductMessage(cluster2, edge);
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
  public FactorSet getClusterMessages(Cluster cluster) {
    throw new UnsupportedOperationException("not implemented; not required for sum product algorithm");
  }

  @Override
  protected Message newMessage(Cluster sourceCluster, Edge edge) {
    return new SumProductMessage(sourceCluster, edge);
  }

}
