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

import java.util.HashSet;
import java.util.Set;


/**
 * @author Thorben
 *
 */
public class FactorSet {

  protected Set<DiscreteFactor> factors;

  public FactorSet(Set<DiscreteFactor> factors) {
    factors = new HashSet<DiscreteFactor>(factors);
  }

  public FactorSet() {
    factors = new HashSet<DiscreteFactor>();
  }

  public DiscreteFactor toFactor() {
    return FactorUtil.jointDistribution(factors);
  }

  public void division(FactorSet other) {
    Set<DiscreteFactor> newFactors = new HashSet<DiscreteFactor>();
    Set<DiscreteFactor> factorsToReplace = new HashSet<DiscreteFactor>();

    for (DiscreteFactor otherFactor : other.factors) {
      boolean factorProcessed = false;

      for (DiscreteFactor thisFactor : factors) {
        if (thisFactor.getVariables().contains(otherFactor.getVariables())) {
          factorsToReplace.add(thisFactor);
          newFactors.add(thisFactor.division(otherFactor));

          factorProcessed = true;
          break;
        }
      }

      if (!factorProcessed) {
        newFactors.add(otherFactor.invert());
      }
    }

    factors.removeAll(factorsToReplace);
    factors.addAll(newFactors);
  }

  public void product(FactorSet other) {
    Set<DiscreteFactor> newFactors = new HashSet<DiscreteFactor>();
    Set<DiscreteFactor> factorsToReplace = new HashSet<DiscreteFactor>();

    for (DiscreteFactor otherFactor : other.factors) {
      boolean factorProcessed = false;

      for (DiscreteFactor thisFactor : factors) {
        if (thisFactor.getVariables().contains(otherFactor.getVariables())) {
          factorsToReplace.add(thisFactor);
          newFactors.add(thisFactor.product(otherFactor));

          factorProcessed = true;
          break;
        }
      }

      if (!factorProcessed) {
        newFactors.add(otherFactor);
      }
    }

    factors.removeAll(factorsToReplace);
    factors.addAll(newFactors);
  }

  public Set<DiscreteFactor> getFactors() {
    return factors;
  }

  public void add(DiscreteFactor factor) {
    this.factors.add(factor);
  }
}
