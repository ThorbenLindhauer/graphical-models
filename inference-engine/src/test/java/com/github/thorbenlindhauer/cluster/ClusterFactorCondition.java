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
package com.github.thorbenlindhauer.cluster;

import org.assertj.core.api.Condition;

import com.github.thorbenlindhauer.factor.DiscreteFactor;

public class ClusterFactorCondition extends Condition<Cluster> {

  protected DiscreteFactor factor;
  
  public ClusterFactorCondition(DiscreteFactor factor) {
    this.factor = factor;
  }
  
  @Override
  public boolean matches(Cluster cluster) {
    return cluster.getFactors().contains(factor) && cluster.getScope().contains(factor.getVariables());
  }
}
