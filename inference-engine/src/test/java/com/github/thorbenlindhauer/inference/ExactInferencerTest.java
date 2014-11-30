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

import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.test.util.TestConstants;

public abstract class ExactInferencerTest {

  protected GraphicalModel bayesianNetwork;
  protected GraphicalModel markovNetwork;

  protected abstract ExactInferencer getInferencer(GraphicalModel model);

  /**
   * Creates a model like A -> C <- B, where there is a CPD P(C | A, B)
   * and two priors P(A) and P(B).
   */
  @Before
  public void setUp() {
    bayesianNetwork = GraphicalModel.create()
        .variable("A", 3).variable("B", 3).variable("C", 2).done()
        .factor()
          .scope("A")
          .basedOnTable(new double[] {0.1, 0.4, 0.5})
        .factor()
          .scope("B")
          .basedOnTable(new double[] {0.5, 0.2, 0.3})
        .factor()
          .scope("A", "B", "C")
          .basedOnTable(new double[] {
            0.3, 0.6, 0.7,  // B == 0, C == 0
            0.2, 0.6, 0.8,  // B == 1, C == 0
            0.25, 0.6, 0.7, // B == 2, C == 0
            0.7, 0.4, 0.3,  // B == 0, C == 1
            0.8, 0.4, 0.2,  // B == 1, C == 1
            0.75, 0.4, 0.3  // B == 2, C == 1
          })
        .build();

    markovNetwork = GraphicalModel.create()
        .variable("A", 3).variable("B", 3).variable("C", 2).done()
        .factor()
          .scope("A", "B")
          .basedOnTable(new double[]{ 10, 1, 1, 1, 10, 1, 1, 1, 10 })
        .factor()
          .scope("B", "C")
          .basedOnTable(new double[]{ 10, 1, 1, 1, 10, 1 })
        .factor()
          .scope("C")
          .basedOnTable(new double[]{ 5, 3 })
        .build();
  }


  @Test
  public void testBayesianNetworkSimpleNaiveInference() {
    ExactInferencer inferencer = getInferencer(bayesianNetwork);
    double jointZeroProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("C"), new int[] {0});
    assertThat(jointZeroProbability).isEqualTo(0.6265d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointOneProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("C"), new int[] {1});
    assertThat(jointOneProbability).isEqualTo(0.3735d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  @Test
  public void testBayesianNetworkSimpleNaiveInferenceWithObservations() {
    ExactInferencer inferencer = getInferencer(bayesianNetwork);
    double jointZeroProbability = inferencer.jointProbability(
        bayesianNetwork.getScope().subScope("A"), new int[] {0}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointZeroProbability).isEqualTo(0.0735d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointOneProbability = inferencer.jointProbability(
        bayesianNetwork.getScope().subScope("A"), new int[] {1}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointOneProbability).isEqualTo(0.16d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointTwoProbability = inferencer.jointProbability(
        bayesianNetwork.getScope().subScope("A"), new int[] {2}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointTwoProbability).isEqualTo(0.14d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  @Test
  public void testBayesianNetworkSimpleNaiveInferenceConditionedOnObservations() {
    ExactInferencer inferencer = getInferencer(bayesianNetwork);
    double jointZeroProbability = inferencer.jointProbabilityConditionedOn(
        bayesianNetwork.getScope().subScope("A"), new int[] {0}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointZeroProbability).isEqualTo(0.196787149d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointOneProbability = inferencer.jointProbabilityConditionedOn(
        bayesianNetwork.getScope().subScope("A"), new int[] {1}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointOneProbability).isEqualTo(0.428380187d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointTwoProbability = inferencer.jointProbabilityConditionedOn(
        bayesianNetwork.getScope().subScope("A"), new int[] {2}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointTwoProbability).isEqualTo(0.374832664d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  // TODO: test some distribution P(A | B=b, C=c), i.e. with two variables conditioned on

  @Test
  public void testMarkovNetworkSimpleNaiveInference() {
    ExactInferencer inferencer = getInferencer(markovNetwork);

    double jointAZeroProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("A"), new int[] {0});
    assertThat(jointAZeroProbability).isEqualTo(0.497395833d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointAOneProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("A"), new int[] {1});
    assertThat(jointAOneProbability).isEqualTo(0.356770833d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointATwoProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("A"), new int[] {2});
    assertThat(jointATwoProbability).isEqualTo(0.145833333d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointBZeroProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("B"), new int[] {0});
    assertThat(jointBZeroProbability).isEqualTo(0.552083333d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointBOneProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("B"), new int[] {1});
    assertThat(jointBOneProbability).isEqualTo(0.364583333, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointBTwoProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("B"), new int[] {2});
    assertThat(jointBTwoProbability).isEqualTo(0.083333333d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointCZeroProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("C"), new int[] {0});
    assertThat(jointCZeroProbability).isEqualTo(0.625d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointCOneProbability = inferencer.jointProbability(bayesianNetwork.getScope().subScope("C"), new int[] {1});
    assertThat(jointCOneProbability).isEqualTo(0.375d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  @Test
  public void testMarkovNetworkSimpleNaiveInferenceWithObservations() {
    ExactInferencer inferencer = getInferencer(markovNetwork);
    double jointZeroProbability = inferencer.jointProbability(
        bayesianNetwork.getScope().subScope("B"), new int[] {0}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointZeroProbability).isEqualTo(0.03125d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointOneProbability = inferencer.jointProbability(
        bayesianNetwork.getScope().subScope("B"), new int[] {1}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointOneProbability).isEqualTo(0.3125d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointTwoProbability = inferencer.jointProbability(
        bayesianNetwork.getScope().subScope("B"), new int[] {2}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointTwoProbability).isEqualTo(0.03125d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  @Test
  public void testMarkovNetworkSimpleNaiveInferenceConditionedOnObservations() {
    ExactInferencer inferencer = getInferencer(markovNetwork);
    double jointZeroProbability = inferencer.jointProbabilityConditionedOn(
        bayesianNetwork.getScope().subScope("B"), new int[] {0}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointZeroProbability).isEqualTo(0.08333333d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointOneProbability = inferencer.jointProbabilityConditionedOn(
        bayesianNetwork.getScope().subScope("B"), new int[] {1}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointOneProbability).isEqualTo(0.83333333d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double jointTwoProbability = inferencer.jointProbabilityConditionedOn(
        bayesianNetwork.getScope().subScope("B"), new int[] {2}, // query
        bayesianNetwork.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointTwoProbability).isEqualTo(0.08333333d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }
}
