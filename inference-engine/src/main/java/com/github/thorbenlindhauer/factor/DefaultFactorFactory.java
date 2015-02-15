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
package com.github.thorbenlindhauer.factor;

import java.util.Arrays;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public interface DefaultFactorFactory<T extends Factor<T>> {

  T build(Scope scope);

  public static class DefaultDiscreteFactorFactory implements DefaultFactorFactory<DiscreteFactor> {

    @Override
    public DiscreteFactor build(Scope scope) {
      double[] values = new double[scope.getNumDistinctValues()];
      Arrays.fill(values, 1);
      DiscreteFactor constantFactor = new TableBasedDiscreteFactor(scope, values);
      return constantFactor;
    }

  }

  public static class DefaultGaussianFactorFactory implements DefaultFactorFactory<GaussianFactor> {

    @Override
    public GaussianFactor build(Scope scope) {
      RealMatrix precisionMatrix = new Array2DRowRealMatrix(scope.size(), scope.size());
      RealVector meanVector = new ArrayRealVector(scope.size());
      GaussianFactor factor = new CanonicalGaussianFactor(scope, precisionMatrix, meanVector, 0.0d);
      return factor;
    }

  }
}
