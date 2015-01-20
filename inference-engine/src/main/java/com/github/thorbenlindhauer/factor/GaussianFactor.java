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

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.github.thorbenlindhauer.variable.Scope;



/**
 * @author Thorben
 *
 */
public interface GaussianFactor extends Factor<GaussianFactor> {

  RealMatrix getCovarianceMatrix();

  RealVector getMeanVector();

  RealMatrix getPrecisionMatrix();

  RealVector getScaledMeanVector();

  double getNormalizationConstant();

  double getValueForAssignment(double[] assignment);

  /**
   * Actually reduces the vector, i.e. removes the observed variables from the scope
   */
  GaussianFactor observation(Scope scope, double[] values);

}
