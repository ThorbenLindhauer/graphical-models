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
package com.github.thorbenlindhauer.inference;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.cluster.messagepassing.BeliefUpdateContextFactory;
import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.inference.loopy.MessageInstruction;
import com.github.thorbenlindhauer.inference.loopy.StaticCalibrationContextFactory;
import com.github.thorbenlindhauer.network.StandaloneGaussiaFactorBuilder;
import com.github.thorbenlindhauer.test.util.LinearAlgebraUtil;
import com.github.thorbenlindhauer.test.util.TestConstants;
import com.github.thorbenlindhauer.variable.ContinuousVariable;

/**
 * @author Thorben
 *
 */
public class GaussianModelInferencerTest {

  ClusterGraph<GaussianFactor> clusterGraph;

  Cluster<GaussianFactor> barometricCluster;
  Cluster<GaussianFactor> temperatureCluster;
  Cluster<GaussianFactor> rainCluster;

  Edge<GaussianFactor> tempRainEdge;
  Edge<GaussianFactor> baroRainEdge;


  @Before
  public void setUp() {
    StandaloneGaussiaFactorBuilder builder = StandaloneGaussiaFactorBuilder.withVariables(
        new ContinuousVariable("RainAmount"),
        new ContinuousVariable("Temperature"),
        new ContinuousVariable("BarometricPressure"));

    // the following factors make no sense meteorologically ;)
    GaussianFactor barometricFactor =
        builder
          .scope("BarometricPressure")
          .momentForm()
          .parameters(LinearAlgebraUtil.asVector(100.0d), LinearAlgebraUtil.asMatrix(10.0d));

    GaussianFactor temperatureFactor =
        builder
          .scope("Temperature")
          .momentForm()
          .parameters(LinearAlgebraUtil.asVector(15.0d), LinearAlgebraUtil.asMatrix(12.0d));

    GaussianFactor rainFactor =
        builder
        .scope("RainAmount", "BarometricPressure", "Temperature")
        .conditional()
        .conditioningScope("BarometricPressure", "Temperature")
        .parameters(LinearAlgebraUtil.asVector(900.0d),
            LinearAlgebraUtil.asMatrix(50.0d),
            LinearAlgebraUtil.asRowMatrix(0.1d, 2.0d));

    barometricCluster = new Cluster<GaussianFactor>(Collections.singleton(barometricFactor));
    temperatureCluster = new Cluster<GaussianFactor>(Collections.singleton(temperatureFactor));
    rainCluster = new Cluster<GaussianFactor>(Collections.singleton(rainFactor));

    Set<Cluster<GaussianFactor>> clusters = new HashSet<Cluster<GaussianFactor>>();
    clusters.add(barometricCluster);
    clusters.add(temperatureCluster);
    clusters.add(rainCluster);

    clusterGraph = new ClusterGraph<GaussianFactor>(clusters);
    tempRainEdge = clusterGraph.connect(temperatureCluster, rainCluster);
    baroRainEdge = clusterGraph.connect(barometricCluster, rainCluster);


  }

  @Test
  public void testPointEstimate() {
    List<MessageInstruction> propagationOrder = new ArrayList<MessageInstruction>();
    propagationOrder.add(new MessageInstruction(tempRainEdge, temperatureCluster));
    propagationOrder.add(new MessageInstruction(baroRainEdge, rainCluster));
    propagationOrder.add(new MessageInstruction(baroRainEdge, barometricCluster));
    propagationOrder.add(new MessageInstruction(tempRainEdge, rainCluster));

    GaussianModelInferencer inferencer = new GaussianClusterGraphInferencer(clusterGraph,
        new BeliefUpdateContextFactory(),
        new StaticCalibrationContextFactory(propagationOrder));

    double[] marginalRainMean = inferencer.posteriorMean(clusterGraph.getScope().subScope("RainAmount"));
    assertThat(marginalRainMean).hasSize(1);
    assertThat(marginalRainMean[0]).isEqualTo(940.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double[][] marginalRainVariance = inferencer.posteriorCovariance(clusterGraph.getScope().subScope("RainAmount"));
    assertThat(marginalRainVariance).hasSize(1);
    assertThat(marginalRainVariance[0]).hasSize(1);
    assertThat(marginalRainVariance[0][0]).isEqualTo(98.1d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double marginalRainProbability = inferencer.jointProbability(clusterGraph.getScope().subScope("RainAmount"), new double[]{950.0d});
    assertThat(marginalRainProbability).isEqualTo(0.0241948, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

}
