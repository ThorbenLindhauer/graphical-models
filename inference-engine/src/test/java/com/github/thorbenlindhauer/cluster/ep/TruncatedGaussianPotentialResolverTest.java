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
package com.github.thorbenlindhauer.cluster.ep;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import com.github.thorbenlindhauer.factor.FactorSet;
import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.network.StandaloneGaussiaFactorBuilder;
import com.github.thorbenlindhauer.test.util.TestConstants;
import com.github.thorbenlindhauer.variable.ContinuousVariable;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public class TruncatedGaussianPotentialResolverTest {

  @Test
  public void testTwoSidedTruncatedGaussianApproximation() {

    ContinuousVariable variable = new ContinuousVariable("A");
    Scope scope = new Scope(Collections.singleton(variable));

    GaussianFactor factor =
        StandaloneGaussiaFactorBuilder.withVariables(variable).scope("A").momentForm()
          .parameters(new ArrayRealVector(new double[] { 2.0d }), new Array2DRowRealMatrix(new double[]{ 4.0d }));

    TruncatedGaussianPotentialResolver resolver = new TruncatedGaussianPotentialResolver(variable, 0.5d, 6.0d);

    FactorSet<GaussianFactor> factorSet = new FactorSet<GaussianFactor>(Collections.singleton(factor));
    FactorSet<GaussianFactor> approximation = resolver.project(factorSet, scope);

    assertThat(approximation.getFactors()).hasSize(1);

    GaussianFactor approximationFactor = approximation.getFactors().iterator().next();
    RealVector meanVector = approximationFactor.getMeanVector();
    RealMatrix covarianceMatrix = approximationFactor.getCovarianceMatrix();

    assertThat(meanVector.getDimension()).isEqualTo(1);
    assertThat(meanVector.getEntry(0)).isEqualTo(2.658510664d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    assertThat(covarianceMatrix.isSquare());
    assertThat(covarianceMatrix.getColumnDimension()).isEqualTo(1);

    assertThat(covarianceMatrix.getEntry(0, 0)).isEqualTo(1.787386921d, TestConstants.DOUBLE_VALUE_TOLERANCE);

  }
}
