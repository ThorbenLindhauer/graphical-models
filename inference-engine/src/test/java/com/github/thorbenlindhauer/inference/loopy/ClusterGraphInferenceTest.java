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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.BeliefUpdateContextFactory;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.cluster.messagepassing.SumProductContextFactory;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.inference.DiscreteClusterGraphInferencer;
import com.github.thorbenlindhauer.inference.loopy.PrioritizedCalibrationContext.PrioritizedCalibrationContextFactory;
import com.github.thorbenlindhauer.inference.loopy.RoundRobinCalibrationContext.RoundRobinCalibrationContextFactory;
import com.github.thorbenlindhauer.network.StandaloneDiscreteFactorBuilder;
import com.github.thorbenlindhauer.test.util.TestConstants;
import com.github.thorbenlindhauer.variable.DiscreteVariable;

/**
 * @author Thorben
 *
 */
@RunWith(Parameterized.class)
public class ClusterGraphInferenceTest {

  protected ClusterGraph<DiscreteFactor> clusterGraph;
  protected DiscreteFactor fullFactor;

  @Parameters
  public static Iterable<Object[]> getCases() {
    return Arrays.asList(
      new Object[][] {
        { new SumProductContextFactory(), new RoundRobinCalibrationContextFactory<DiscreteFactor>(new DiscreteFactorEvaluator()) },
        { new BeliefUpdateContextFactory(), new RoundRobinCalibrationContextFactory<DiscreteFactor>(new DiscreteFactorEvaluator()) },
        { new SumProductContextFactory(), new PrioritizedCalibrationContextFactory<DiscreteFactor>(new DiscreteFactorEvaluator()) },
        { new BeliefUpdateContextFactory(), new PrioritizedCalibrationContextFactory<DiscreteFactor>(new DiscreteFactorEvaluator()) }
      }
    );
  }

  protected MessagePassingContextFactory messagePassingContextFactory;
  protected ClusterGraphCalibrationContextFactory<DiscreteFactor> calibrationContextFactory;

  public ClusterGraphInferenceTest(MessagePassingContextFactory messagePassingContextFactory,
      ClusterGraphCalibrationContextFactory<DiscreteFactor> calibrationContextFactory) {
    this.messagePassingContextFactory = messagePassingContextFactory;
    this.calibrationContextFactory = calibrationContextFactory;
  }

  @Before
  public void setUp() {
    // constructs a pairwise markov network over four variables
    StandaloneDiscreteFactorBuilder factorBuilder =
        StandaloneDiscreteFactorBuilder.withVariables(
            new DiscreteVariable("A", 2),
            new DiscreteVariable("B", 2),
            new DiscreteVariable("C", 2),
            new DiscreteVariable("D", 2));

    Set<Cluster<DiscreteFactor>> clusters = new HashSet<Cluster<DiscreteFactor>>();

    // single variable potentials
    Cluster<DiscreteFactor> aCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("A").basedOnTable(new double[] { 0.1, 0.9 })
        ));
    Cluster<DiscreteFactor> bCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("B").basedOnTable(new double[] { 0.5, 0.5 })
        ));
    Cluster<DiscreteFactor> cCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("C").basedOnTable(new double[] { 0.5, 0.5 })
        ));
    Cluster<DiscreteFactor> dCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("D").basedOnTable(new double[] { 0.1, 0.9 })
        ));

    // variable pairs
    Cluster<DiscreteFactor> abCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("A", "B").basedOnTable(new double[] { 0.3, 0.7, 0.3, 0.7 })
        ));
    Cluster<DiscreteFactor> bcCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("B", "C").basedOnTable(new double[] { 0.4, 0.6, 0.4, 0.6})
        ));
    Cluster<DiscreteFactor> cdCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("C", "D").basedOnTable(new double[] { 0.4, 0.6, 0.4, 0.6 })
        ));
    Cluster<DiscreteFactor> adCluster = new Cluster<DiscreteFactor>(Sets.newLinkedHashSet(
        factorBuilder.scope("A", "D").basedOnTable(new double[] { 0.1, 0.1, 0.1, 0.9})
        ));

    clusters.add(aCluster);
    clusters.add(bCluster);
    clusters.add(cCluster);
    clusters.add(dCluster);
    clusters.add(abCluster);
    clusters.add(bcCluster);
    clusters.add(cdCluster);
    clusters.add(adCluster);

    clusterGraph = new ClusterGraph<DiscreteFactor>(clusters);
    clusterGraph.connect(aCluster, abCluster);
    clusterGraph.connect(abCluster, bCluster);
    clusterGraph.connect(bCluster, bcCluster);
    clusterGraph.connect(bcCluster, cCluster);
    clusterGraph.connect(cCluster, cdCluster);
    clusterGraph.connect(cdCluster, dCluster);
    clusterGraph.connect(dCluster, adCluster);
    clusterGraph.connect(adCluster, aCluster);

    fullFactor = computeJointDistribution(clusterGraph);
  }

  protected DiscreteFactor computeJointDistribution(ClusterGraph<DiscreteFactor> clusterGraph) {
    DiscreteFactor fullFactor = null;

    for (Cluster<DiscreteFactor> cluster : clusterGraph.getClusters()) {
      for (DiscreteFactor factor : cluster.getFactors()) {
        if (fullFactor == null) {
          fullFactor = factor;
        } else {
          fullFactor = fullFactor.product(factor);
        }
      }
    }

    fullFactor = fullFactor.normalize();
    return fullFactor;
  }

  @Test
  public void testApproximateInference() {
    DiscreteClusterGraphInferencer inferencer = new DiscreteClusterGraphInferencer(clusterGraph, messagePassingContextFactory, calibrationContextFactory);

    DiscreteFactor exactAMarginal = fullFactor.marginal(clusterGraph.getScope().subScope("A"));
    double expectedA0Prob = exactAMarginal.getValueAtIndex(0);
    double expectedA1Prob = exactAMarginal.getValueAtIndex(1);

    double a0Prob = inferencer.jointProbability(clusterGraph.getScope().subScope("A"), new int[]{ 0 });
    assertThat(a0Prob).isEqualTo(expectedA0Prob, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double a1Prob = inferencer.jointProbability(clusterGraph.getScope().subScope("A"), new int[]{ 1 });
    assertThat(a1Prob).isEqualTo(expectedA1Prob, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }
}
