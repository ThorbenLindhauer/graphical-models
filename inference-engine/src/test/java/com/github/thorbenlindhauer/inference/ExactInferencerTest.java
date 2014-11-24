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

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.network.GraphicalModel;

public abstract class ExactInferencerTest {
  
  protected static final Offset<Double> DOUBLE_VALUE_TOLERANCE = Offset.offset(0.00001d);

  protected GraphicalModel model;
  
  protected abstract ExactInferencer getInferencer();
  
  /**
   * Creates a model like A -> C <- B, where there is a CPD P(C | A, B)
   * and two priors P(A) and P(B).
   */
  @Before
  public void setUp() {
    model = GraphicalModel.create()
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
  }
  
  
  @Test
  public void testSimpleNaiveInference() {
    ExactInferencer inferencer = getInferencer();
    double jointZeroProbability = inferencer.jointProbability(model.getScope().subScope("C"), new int[] {0});
    assertThat(jointZeroProbability).isEqualTo(0.6265d, DOUBLE_VALUE_TOLERANCE);
    
    double jointOneProbability = inferencer.jointProbability(model.getScope().subScope("C"), new int[] {1});
    assertThat(jointOneProbability).isEqualTo(0.3735d, DOUBLE_VALUE_TOLERANCE);
  }
  
  @Test
  public void testSimpleNaiveInferenceWithObservations() {
    ExactInferencer inferencer = getInferencer();
    double jointZeroProbability = inferencer.jointProbability(
        model.getScope().subScope("A"), new int[] {0}, // query
        model.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointZeroProbability).isEqualTo(0.0735d, DOUBLE_VALUE_TOLERANCE);
    
    double jointOneProbability = inferencer.jointProbability(
        model.getScope().subScope("A"), new int[] {1}, // query
        model.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointOneProbability).isEqualTo(0.16d, DOUBLE_VALUE_TOLERANCE);
    
    double jointTwoProbability = inferencer.jointProbability(
        model.getScope().subScope("A"), new int[] {2}, // query
        model.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointTwoProbability).isEqualTo(0.14d, DOUBLE_VALUE_TOLERANCE);
  }
  
  @Test
  public void testSimpleNaiveInferenceConditionedOnObservations() {
    ExactInferencer inferencer = getInferencer();
    double jointZeroProbability = inferencer.jointProbabilityConditionedOn(
        model.getScope().subScope("A"), new int[] {0}, // query
        model.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointZeroProbability).isEqualTo(0.196787149d, DOUBLE_VALUE_TOLERANCE);
    
    double jointOneProbability = inferencer.jointProbabilityConditionedOn(
        model.getScope().subScope("A"), new int[] {1}, // query
        model.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointOneProbability).isEqualTo(0.428380187d, DOUBLE_VALUE_TOLERANCE);
    
    double jointTwoProbability = inferencer.jointProbabilityConditionedOn(
        model.getScope().subScope("A"), new int[] {2}, // query
        model.getScope().subScope("C"), new int[] {1} // observation
      );
    assertThat(jointTwoProbability).isEqualTo(0.374832664d, DOUBLE_VALUE_TOLERANCE);
  }
  
  // TODO: test some distribution P(A | B=b, C=c), i.e. with two variables conditioned on
}
