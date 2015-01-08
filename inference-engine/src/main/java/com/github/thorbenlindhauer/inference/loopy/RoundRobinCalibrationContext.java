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
package com.github.thorbenlindhauer.inference.loopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.cluster.messagepassing.Message;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContext;
import com.github.thorbenlindhauer.factor.Factor;

/**
 * @author Thorben
 *
 */
public class RoundRobinCalibrationContext<T extends Factor<T>> implements ClusterGraphCalibrationContext<T> {

  protected List<Message<T>> messages;
  protected Iterator<Message<T>> messageIt;
  protected boolean isCurrentRoundCalibrated;
  protected MessagePassingContext<T> messagePassingContext;
  protected FactorEvaluator<T> factorEvaluator;

  public RoundRobinCalibrationContext(ClusterGraph<T> clusterGraph, MessagePassingContext<T> messagePassingContext, FactorEvaluator<T> factorEvaluator) {
    this.messages = new ArrayList<Message<T>>();

    for (Edge<T> edge : clusterGraph.getEdges()) {
      this.messages.add(messagePassingContext.getMessage(edge, edge.getCluster1()));
      this.messages.add(messagePassingContext.getMessage(edge, edge.getCluster2()));
    }

    this.messagePassingContext = messagePassingContext;
    this.factorEvaluator = factorEvaluator;
    resetIterator();
  }

  /**
   * @return null when all messages are calibrated
   */
  public Message<T> getNextUncalibratedMessage() {
    if (messageIt.hasNext()) {
      Message<T> nextMessage = messageIt.next();
      Edge<T> edge = nextMessage.getEdge();

      if (!isCalibrated(edge)) {
        isCurrentRoundCalibrated = false;
        return nextMessage;
      } else {
        return getNextUncalibratedMessage();
      }

    } else {
      if (isCurrentRoundCalibrated) {
        return null;
      }

      resetIterator();
      return getNextUncalibratedMessage();
    }
  }

  protected void resetIterator() {
    messageIt = messages.iterator();
    isCurrentRoundCalibrated = true;
  }

  protected boolean isCalibrated(Edge<T> edge) {
    Cluster<T> cluster1 = edge.getCluster1();
    Cluster<T> cluster2 = edge.getCluster2();
    T cluster1Potential = messagePassingContext.getClusterPotential(cluster1).marginal(edge.getScope());
    T cluster2Potential = messagePassingContext.getClusterPotential(cluster2).marginal(edge.getScope());

    return factorEvaluator.equalFactors(cluster1Potential, cluster2Potential);
  }

  @Override
  public void notify(String eventName, Message<T> object) {
    // do nothing
  }

  public static class RoundRobinCalibrationContextFactory<T extends Factor<T>>  implements ClusterGraphCalibrationContextFactory<T> {

    protected FactorEvaluator<T> factorEvaluator;

    public RoundRobinCalibrationContextFactory(FactorEvaluator<T> factorEvaluator) {
      this.factorEvaluator = factorEvaluator;
    }

    @Override
    public ClusterGraphCalibrationContext<T> buildCalibrationContext(ClusterGraph<T> clusterGraph, MessagePassingContext<T> messagePassingContext) {
      return new RoundRobinCalibrationContext<T>(clusterGraph, messagePassingContext, factorEvaluator);
    }

  }


}
