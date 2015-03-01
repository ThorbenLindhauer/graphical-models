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

import com.github.thorbenlindhauer.learning.distribution.DirichletDistribution;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public interface DirichletPriorInitializer {

  void initialize(DirichletDistribution prior, Scope describingScope, Scope conditioningScope, int[] conditioningAssignment);

}
