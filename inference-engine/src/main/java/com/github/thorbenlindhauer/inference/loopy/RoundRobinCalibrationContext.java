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
import com.github.thorbenlindhauer.factor.DiscreteFactor;

/**
 * @author Thorben
 *
 */
public class RoundRobinCalibrationContext implements ClusterGraphCalibrationContext {

  protected static final double COMPARISON_PRECISION = 10e-3;

  protected List<Message> messages;
  protected Iterator<Message> messageIt;
  protected boolean isCurrentRoundCalibrated;
  protected MessagePassingContext messagePassingContext;

  public RoundRobinCalibrationContext(ClusterGraph clusterGraph, MessagePassingContext messagePassingContext) {
    this.messages = new ArrayList<Message>();

    for (Edge edge : clusterGraph.getEdges()) {
      this.messages.add(messagePassingContext.getMessage(edge, edge.getCluster1()));
      this.messages.add(messagePassingContext.getMessage(edge, edge.getCluster2()));
    }

    this.messagePassingContext = messagePassingContext;
    resetIterator();
  }

  /**
   * @return null when all messages are calibrated
   */
  public Message getNextUncalibratedMessage() {
    if (messageIt.hasNext()) {
      Message nextMessage = messageIt.next();
      Edge edge = nextMessage.getEdge();

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

  protected boolean isCalibrated(Edge edge) {
    Cluster cluster1 = edge.getCluster1();
    Cluster cluster2 = edge.getCluster2();
    DiscreteFactor cluster1Potential = messagePassingContext.getClusterPotential(cluster1).marginal(edge.getScope());
    DiscreteFactor cluster2Potential = messagePassingContext.getClusterPotential(cluster2).marginal(edge.getScope());

    for (int i = 0; i < cluster1Potential.getVariables().getNumDistinctValues(); i++) {
      double valueDiff = cluster1Potential.getValueAtIndex(i) - cluster2Potential.getValueAtIndex(i);
      if (valueDiff > COMPARISON_PRECISION || valueDiff < - COMPARISON_PRECISION) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void notify(String eventName, Message object) {
    // do nothing
  }

  public static class RoundRobinCalibrationContextFactory implements ClusterGraphCalibrationContextFactory {

    @Override
    public ClusterGraphCalibrationContext buildCalibrationContext(ClusterGraph clusterGraph, MessagePassingContext messagePassingContext) {
      return new RoundRobinCalibrationContext(clusterGraph, messagePassingContext);
    }

  }


}
