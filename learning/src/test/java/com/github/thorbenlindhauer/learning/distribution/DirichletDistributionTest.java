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
package com.github.thorbenlindhauer.learning.distribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

import com.github.thorbenlindhauer.learning.DistributionStructureException;

/**
 * @author Thorben
 *
 */
public class DirichletDistributionTest {

  @Test
  public void testGetParameters() {
    DirichletDistribution distribution = new DirichletDistribution(3);
    distribution.setParameter(0, 3);
    distribution.setParameter(1, 5);
    distribution.setParameter(2, 6);

    distribution.submitEvidence(1, 2);

    double[] parameters = distribution.getParameters();
    assertThat(parameters).isEqualTo(new double[]{ 3.0d, 7.0d, 6.0d });

  }

  @Test
  public void testCannotSetParameterOutOfBounds() {
    DirichletDistribution distribution = new DirichletDistribution(3);
    try {
      distribution.setParameter(3, 1);
      fail("exception expected");
    } catch (DistributionStructureException e) {
      // expected
    }

    try {
      distribution.submitEvidence(3, 1);
      fail("exception expected");
    } catch (DistributionStructureException e) {
      // expected
    }
  }

  @Test
  public void testCannotSubmitNegativeParameterValue() {
    DirichletDistribution distribution = new DirichletDistribution(3);
    try {
      distribution.setParameter(0, -1);
      fail("exception expected");
    } catch (DistributionStructureException e) {
      // expected
    }
  }

  @Test
  public void testExpectation() {
    DirichletDistribution distribution = new DirichletDistribution(3);
    distribution.setParameter(0, 4);
    distribution.setParameter(1, 4);
    distribution.setParameter(2, 6);

    distribution.submitEvidence(1, 2);

    double[] expectation = distribution.getExpectation();
    assertThat(expectation).isEqualTo(new double[]{ 0.25d, 0.375d, 0.375d });
  }
}
