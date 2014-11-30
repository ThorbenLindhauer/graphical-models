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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

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
public class PrioritizedCalibrationContext implements ClusterGraphCalibrationContext {

  protected static final double COMPARISON_PRECISION = 10e-50;

  protected ClusterGraph clusterGraph;
  protected MessagePassingContext messagePassingContext;

  protected PriorityQueue<EdgeCalibration> edgeCalibrationQueue;
  protected Map<Edge, EdgeCalibration> edgeCalibrationIndex;

  public PrioritizedCalibrationContext(ClusterGraph clusterGraph, MessagePassingContext messagePassingContext) {
    this.clusterGraph = clusterGraph;
    this.messagePassingContext = messagePassingContext;
    initEdgeCalibrationQueue();
  }

  protected void initEdgeCalibrationQueue() {
    this.edgeCalibrationQueue = new PriorityQueue<EdgeCalibration>(clusterGraph.getEdges().size() * 2,
        new EdgeCalibrationComparator());
    this.edgeCalibrationIndex = new HashMap<Edge, EdgeCalibration>();

    for (Cluster cluster : clusterGraph.getClusters()) {
      for (Edge edge : cluster.getEdges()) {
        EdgeCalibration edgeCalibration = edgeCalibrationIndex.get(edge);
        if (edgeCalibration == null) {
          edgeCalibration = new EdgeCalibration();
          edgeCalibration.edge = edge;
          edgeCalibration.messagePassingContext = messagePassingContext;
          edgeCalibrationIndex.put(edge, edgeCalibration);
        }

        edgeCalibrationQueue.add(edgeCalibration);
      }
    }
  }

  @Override
  public void notify(String eventName, Message message) {
    Cluster targetCluster = message.getTargetCluster();

    // readd those edges to the queue, for which the involved cluster potentials have changed
    for (Edge edge : targetCluster.getEdges()) {
      EdgeCalibration edgeCalibration = edgeCalibrationIndex.get(edge);
      edgeCalibration.invalidCache = true;

      edgeCalibrationQueue.remove(edgeCalibration);
      edgeCalibrationQueue.add(edgeCalibration);
    }
  }

  @Override
  public Message getNextUncalibratedMessage() {
    EdgeCalibration topCalibration = edgeCalibrationQueue.peek();
    double clusterDisagreement = topCalibration.quantifyDisagreement();

    if (clusterDisagreement < COMPARISON_PRECISION && clusterDisagreement > - COMPARISON_PRECISION) {
      return null;
    } else {
      return topCalibration.getMessage();
    }
  }

  public static class PrioritizedCalibrationContextFactory implements ClusterGraphCalibrationContextFactory {

    @Override
    public ClusterGraphCalibrationContext buildCalibrationContext(ClusterGraph clusterGraph, MessagePassingContext messagePassingContext) {
      return new PrioritizedCalibrationContext(clusterGraph, messagePassingContext);
    }
  }

  protected static class EdgeCalibration {
    protected Edge edge;
    protected MessagePassingContext messagePassingContext;

    protected boolean invalidCache = true;
    protected double cachedDisagreement;
    protected Cluster lastSourceCluster;

    public double quantifyDisagreement() {
      if (invalidCache) {
        DiscreteFactor c1Potential = messagePassingContext.getClusterPotential(edge.getCluster1()).marginal(edge.getScope());
        DiscreteFactor c2Potential = messagePassingContext.getClusterPotential(edge.getCluster2()).marginal(edge.getScope());

        cachedDisagreement = jsDivergence(c1Potential.normalize(), c2Potential.normalize());

        invalidCache = false;
      }

      return cachedDisagreement;
    }

    @Override
    public String toString() {
      return "" + cachedDisagreement;
    }

    protected double jsDivergence(DiscreteFactor distribution1, DiscreteFactor distribution2) {
      double divergence = 0.0d;

      for (int i = 0; i < distribution1.getVariables().getNumDistinctValues(); i++) {
        divergence += 0.5d * distribution1.getValueAtIndex(i) * Math.log(distribution1.getValueAtIndex(i) / distribution2.getValueAtIndex(i));
        divergence += 0.5d * distribution2.getValueAtIndex(i) * Math.log(distribution2.getValueAtIndex(i) / distribution1.getValueAtIndex(i));
      }

      return divergence;
    }

    public Message getMessage() {
      if (lastSourceCluster == null || lastSourceCluster == edge.getCluster2()) {
        lastSourceCluster = edge.getCluster1();
      } else {
        lastSourceCluster = edge.getCluster2();
      }

      return messagePassingContext.getMessage(edge, lastSourceCluster);
    }
  }

  protected static class EdgeCalibrationComparator implements Comparator<EdgeCalibration> {

    @Override
    public int compare(EdgeCalibration o1, EdgeCalibration o2) {
      if (o1 == o2) {
        return 0;
      }

      if (o1 == null) {
        return 1;
      } else if (o2 == null) {
        return -1;
      } else {
        double disagreement1 = o1.quantifyDisagreement();
        double disagreement2 = o2.quantifyDisagreement();

        if (disagreement1 < disagreement2) {
          return 1;
        } else {
          if (disagreement1 == disagreement2) {
            int o1Hash = o1.hashCode();
            int o2Hash = o2.hashCode();

            if (o1Hash < o2Hash) {
              return 1;
            } else {
              if (o1Hash == o2Hash) {
                return 0;
              }

              return -1;
            }
          }

          return -1;
        }
      }
    }
  }


}
