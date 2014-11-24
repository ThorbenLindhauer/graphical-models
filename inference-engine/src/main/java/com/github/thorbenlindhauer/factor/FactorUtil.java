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

import java.util.Set;

public class FactorUtil {

  public static DiscreteFactor jointDistribution(Set<DiscreteFactor> factors) {
    DiscreteFactor jointDistribution = null;
    
    for (DiscreteFactor factor : factors) {
      if (jointDistribution == null) {
        jointDistribution = factor;
      } else {
        jointDistribution = jointDistribution.product(factor);
      }
    }
    
    return jointDistribution;
  }
}
