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

import com.github.thorbenlindhauer.factor.DiscreteFactor;

/**
 * @author Thorben
 *
 */
public class DiscreteFactorEvaluator implements FactorEvaluator<DiscreteFactor> {

  protected static final double COMPARISON_PRECISION = 10e-3;

  /**
   * implements js-divergence
   */
  @Override
  public double quantifyDisagreement(DiscreteFactor factor1, DiscreteFactor factor2) {
    double divergence = 0.0d;

    for (int i = 0; i < factor1.getVariables().getNumDistinctValues(); i++) {
      divergence += 0.5d * factor1.getValueAtIndex(i) * Math.log(factor1.getValueAtIndex(i) / factor2.getValueAtIndex(i));
      divergence += 0.5d * factor2.getValueAtIndex(i) * Math.log(factor2.getValueAtIndex(i) / factor1.getValueAtIndex(i));
    }

    return divergence;
  }

  @Override
  public boolean equalFactors(DiscreteFactor factor1, DiscreteFactor factor2) {
    for (int i = 0; i < factor1.getVariables().getNumDistinctValues(); i++) {
      double valueDiff = factor1.getValueAtIndex(i) - factor2.getValueAtIndex(i);
      if (valueDiff > COMPARISON_PRECISION || valueDiff < - COMPARISON_PRECISION) {
        return false;
      }
    }

    return true;
  }

}
