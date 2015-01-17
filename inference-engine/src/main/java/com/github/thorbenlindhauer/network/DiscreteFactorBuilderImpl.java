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
package com.github.thorbenlindhauer.network;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

public class DiscreteFactorBuilderImpl extends AbstractFactorBuilderImpl<DiscreteFactorBuilder<DiscreteModelBuilder>>
  implements DiscreteFactorBuilder<DiscreteModelBuilder> {

  protected DiscreteModelBuilderImpl modelBuilder;

  public DiscreteFactorBuilderImpl(DiscreteModelBuilderImpl modelBuilder, Scope scope) {
    super(scope);
    this.modelBuilder = modelBuilder;
  }

  public DiscreteModelBuilder basedOnTable(double[] table) {
    Scope factorScope = new Scope(factorVariables);
    DiscreteFactor factor = new TableBasedDiscreteFactor(factorScope, table);
    modelBuilder.addFactor(factor);

    return modelBuilder;
  }

}
