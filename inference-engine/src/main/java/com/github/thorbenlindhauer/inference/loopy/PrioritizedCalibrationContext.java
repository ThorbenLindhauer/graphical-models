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
import com.github.thorbenlindhauer.factor.Factor;

/**
 * @author Thorben
 *
 */
public class PrioritizedCalibrationContext<T extends Factor<T>> implements ClusterGraphCalibrationContext<T> {

  protected static final double COMPARISON_PRECISION = 10e-50;

  protected ClusterGraph<T> clusterGraph;
  protected MessagePassingContext<T> messagePassingContext;
  protected FactorEvaluator<T> factorEvaluator;

  protected PriorityQueue<EdgeCalibration<T>> edgeCalibrationQueue;
  protected Map<Edge<T>, EdgeCalibration<T>> edgeCalibrationIndex;

  public PrioritizedCalibrationContext(ClusterGraph<T> clusterGraph, MessagePassingContext<T> messagePassingContext, FactorEvaluator<T> factorEvaluator) {
    this.clusterGraph = clusterGraph;
    this.messagePassingContext = messagePassingContext;
    this.factorEvaluator = factorEvaluator;
    initEdgeCalibrationQueue();
  }

  protected void initEdgeCalibrationQueue() {
    this.edgeCalibrationQueue = new PriorityQueue<EdgeCalibration<T>>(clusterGraph.getEdges().size() * 2,
        new EdgeCalibrationComparator());
    this.edgeCalibrationIndex = new HashMap<Edge<T>, EdgeCalibration<T>>();

    for (Cluster<T> cluster : clusterGraph.getClusters()) {
      for (Edge<T> edge : cluster.getEdges()) {
        EdgeCalibration<T> edgeCalibration = edgeCalibrationIndex.get(edge);
        if (edgeCalibration == null) {
          edgeCalibration = new EdgeCalibration<T>(factorEvaluator);
          edgeCalibration.edge = edge;
          edgeCalibration.messagePassingContext = messagePassingContext;
          edgeCalibrationIndex.put(edge, edgeCalibration);
        }

        edgeCalibrationQueue.add(edgeCalibration);
      }
    }
  }

  @Override
  public void notify(String eventName, Message<T> message) {
    Cluster<T> targetCluster = message.getTargetCluster();

    // readd those edges to the queue, for which the involved cluster potentials have changed
    for (Edge<T> edge : targetCluster.getEdges()) {
      EdgeCalibration<T> edgeCalibration = edgeCalibrationIndex.get(edge);
      edgeCalibration.invalidCache = true;

      edgeCalibrationQueue.remove(edgeCalibration);
      edgeCalibrationQueue.add(edgeCalibration);
    }
  }

  @Override
  public Message<T> getNextUncalibratedMessage() {
    EdgeCalibration<T> topCalibration = edgeCalibrationQueue.peek();
    double clusterDisagreement = topCalibration.quantifyDisagreement();

    if (clusterDisagreement < COMPARISON_PRECISION && clusterDisagreement > - COMPARISON_PRECISION) {
      return null;
    } else {
      return topCalibration.getMessage();
    }
  }

  public static class PrioritizedCalibrationContextFactory<T extends Factor<T>> implements ClusterGraphCalibrationContextFactory<T> {

    protected FactorEvaluator<T> factorEvaluator;

    public PrioritizedCalibrationContextFactory(FactorEvaluator<T> factorEvaluator) {
      this.factorEvaluator = factorEvaluator;
    }

    @Override
    public ClusterGraphCalibrationContext<T> buildCalibrationContext(ClusterGraph<T> clusterGraph, MessagePassingContext<T> messagePassingContext) {
      return new PrioritizedCalibrationContext<T>(clusterGraph, messagePassingContext, factorEvaluator);
    }
  }

  protected static class EdgeCalibration<T extends Factor<T>> {
    protected Edge<T> edge;
    protected MessagePassingContext<T> messagePassingContext;

    protected boolean invalidCache = true;
    protected double cachedDisagreement;
    protected Cluster<T> lastSourceCluster;
    protected FactorEvaluator<T> factorEvaluator;

    public EdgeCalibration(FactorEvaluator<T> factorEvaluator) {
      this.factorEvaluator = factorEvaluator;
    }

    public double quantifyDisagreement() {
      if (invalidCache) {
        T c1Potential = messagePassingContext.getClusterPotential(edge.getCluster1()).marginal(edge.getScope());
        T c2Potential = messagePassingContext.getClusterPotential(edge.getCluster2()).marginal(edge.getScope());

        cachedDisagreement = factorEvaluator.quantifyDisagreement(c1Potential.normalize(), c2Potential.normalize());

        invalidCache = false;
      }

      return cachedDisagreement;
    }

    @Override
    public String toString() {
      return "" + cachedDisagreement;
    }

    public Message<T> getMessage() {
      if (lastSourceCluster == null || lastSourceCluster == edge.getCluster2()) {
        lastSourceCluster = edge.getCluster1();
      } else {
        lastSourceCluster = edge.getCluster2();
      }

      return messagePassingContext.getMessage(edge, lastSourceCluster);
    }
  }

  protected static class EdgeCalibrationComparator implements Comparator<EdgeCalibration<?>> {

    @Override
    public int compare(EdgeCalibration<?> o1, EdgeCalibration<?> o2) {
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
