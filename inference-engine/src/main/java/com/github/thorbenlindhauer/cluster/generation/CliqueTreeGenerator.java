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
package com.github.thorbenlindhauer.cluster.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.Listener;
import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.factorgraph.FactorGraph;
import com.github.thorbenlindhauer.graph.operation.MaximumCardinalityCliqueOperation;
import com.github.thorbenlindhauer.graph.operation.MaximumSpanningClusterGraphOperation;
import com.github.thorbenlindhauer.graph.operation.Triangulator;
import com.github.thorbenlindhauer.inference.variableelimination.MinFillEliminationStrategy;
import com.github.thorbenlindhauer.inference.variableelimination.VariableEliminationStrategy;
import com.github.thorbenlindhauer.network.GraphicalModel;

/**
 * Generates a clique tree from a graphical model by the following strategy:
 *
 * <ul>
 *   <li>determine a variable elimination order</li>
 *   <li>moralize graphical model</li>
 *   <li>determine induced (and triangulated) graph based on that order</li>
 *   <li>find maximum cliques by maximum cardinality search</li>
 *   <li>determine edges by maximum spanning tree over the clusters and their sepsets</li>
 * </ul>
 *
 * @author Thorben
 */
public class CliqueTreeGenerator {

  public static final String CLUSTER_GRAPH_CREATED_EVENT = "cluster-graph-created";

  protected Map<String, List<Listener<?>>> listeners = new HashMap<String, List<Listener<?>>>();

  public <T extends Factor<T>> ClusterGraph<T> generateClusterGraph(GraphicalModel<T> graphicalModel) {
    List<String> eliminationOrder = getEliminationStrategy().getEliminationOrder(graphicalModel,
        Arrays.asList(graphicalModel.getScope().getVariableIds()));

    FactorGraph<T> moralizedGraph = FactorGraph.fromGraphicalModel(graphicalModel.getFactors());

    FactorGraph<T> inducedGraph = getTriangulator().getInducedGraph(moralizedGraph, eliminationOrder);

    Set<Cluster<T>> clusters = this.<T>getMaximumCliqueAnalyzer().execute(inducedGraph);

    ClusterGraph<T> clusterGraph = getMaximumSpanningTreeAnalyzer().execute(clusters);

    dispatchEvent(CLUSTER_GRAPH_CREATED_EVENT, clusterGraph);

    return clusterGraph;
  }

  protected VariableEliminationStrategy getEliminationStrategy() {
    return new MinFillEliminationStrategy();
  }

  protected Triangulator getTriangulator() {
    return new Triangulator();
  }

  protected <T extends Factor<T>> MaximumCardinalityCliqueOperation<T> getMaximumCliqueAnalyzer() {
    return new MaximumCardinalityCliqueOperation<T>();
  }

  protected MaximumSpanningClusterGraphOperation getMaximumSpanningTreeAnalyzer() {
    return new MaximumSpanningClusterGraphOperation();
  }

  public void registerListener(String event, Listener<?> listener) {
    List<Listener<?>> listenersForEvent = listeners.get(event);
    if (listenersForEvent == null) {
      listenersForEvent = new ArrayList<Listener<?>>();
      listeners.put(event, listenersForEvent);
    }
    listenersForEvent.add(listener);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void dispatchEvent(String event, Object value) {
    List<Listener<?>> listenersForEvent = listeners.get(event);
    if (listenersForEvent != null) {
      for (Listener listener : listenersForEvent) {
        listener.notify(event, value);
      }
    }
  }
}
