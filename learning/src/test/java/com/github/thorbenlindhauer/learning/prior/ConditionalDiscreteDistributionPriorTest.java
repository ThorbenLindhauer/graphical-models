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
package com.github.thorbenlindhauer.learning.prior;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.thorbenlindhauer.network.ScopeBuilderImpl;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public class ConditionalDiscreteDistributionPriorTest {

  @Test
  public void testMultipleConditionedPriors() {
    Scope distributionScope = new ScopeBuilderImpl()
      .discreteVariable("A", 3)
      .discreteVariable("B", 2)
      .discreteVariable("C", 3)
      .buildScope();

    Scope conditioningScope = distributionScope.subScope("B", "C");

    ConditionalDiscreteDistributionPrior priorManager = new ConditionalDiscreteDistributionPrior(distributionScope, conditioningScope, new BDePriorInitializer(0));
    priorManager.submitEvidence(new int[]{0, 0}, new int[]{1, 4, 1});
    priorManager.submitEvidence(new int[]{1, 0}, new int[]{5, 0, 2});
    priorManager.submitEvidence(new int[]{0, 1}, new int[]{3, 3, 3});
    priorManager.submitEvidence(new int[]{1, 1}, new int[]{9, 2, 1});
    priorManager.submitEvidence(new int[]{0, 2}, new int[]{10, 1, 7});
    priorManager.submitEvidence(new int[]{1, 2}, new int[]{3, 2, 6});

    double[] posteriorValues = priorManager.toCanonicalValueVector();

    assertThat(posteriorValues).hasSize(3 * 2 * 3);
    assertThat(posteriorValues).isEqualTo(new double[]{
          1.0d / 6.0d,  4.0d / 6.0d,  1.0d / 6.0d, // B = 0, C = 0
          5.0d / 7.0d,         0.0d,  2.0d / 7.0d, // B = 1, C = 0
          3.0d / 9.0d,  3.0d / 9.0d,  3.0d / 9.0d, // B = 0, C = 1
         9.0d / 12.0d, 2.0d / 12.0d, 1.0d / 12.0d, // B = 1, C = 1
        10.0d / 18.0d, 1.0d / 18.0d, 7.0d / 18.0d, // B = 0, C = 2
         3.0d / 11.0d, 2.0d / 11.0d, 6.0d / 11.0d  // B = 1, C = 2
    });
  }

  @Test
  public void testMultipleConditionedPriorsCase2() {
    Scope distributionScope = new ScopeBuilderImpl()
      .discreteVariable("A", 3)
      .discreteVariable("B", 2)
      .buildScope();

    Scope conditioningScope = distributionScope.subScope("A");

    ConditionalDiscreteDistributionPrior priorManager = new ConditionalDiscreteDistributionPrior(distributionScope, conditioningScope, new BDePriorInitializer(0));
    priorManager.submitEvidence(new int[]{0}, new int[]{1, 4});
    priorManager.submitEvidence(new int[]{1}, new int[]{5, 0});
    priorManager.submitEvidence(new int[]{2}, new int[]{3, 3});

    double[] posteriorValues = priorManager.toCanonicalValueVector();

    assertThat(posteriorValues).hasSize(3 * 2);
    assertThat(posteriorValues).isEqualTo(new double[]{
          0.2d,  1.0d,  0.5d, // B = 0
          0.8d,  0.0d,  0.5d, // B = 1
    });
  }

  @Test
  public void testBDePriorInitialization() {
    Scope distributionScope = new ScopeBuilderImpl()
      .discreteVariable("A", 2)
      .discreteVariable("B", 3)
      .buildScope();

    Scope conditioningScope = distributionScope.subScope("B");

    ConditionalDiscreteDistributionPrior priorManager =
        new ConditionalDiscreteDistributionPrior(distributionScope, conditioningScope, new BDePriorInitializer(20));

    double[] posterior = priorManager.toCanonicalValueVector();
    assertThat(posterior).hasSize(6);
    assertThat(posterior).isEqualTo(new double[] {
        0.5d, 0.5d, 0.5d, 0.5d, 0.5d, 0.5d
    });
  }

}
