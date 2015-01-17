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

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.variable.Scope;

public class GaussianModelBuilderImpl implements GaussianModelBuilder {

  protected Set<GaussianFactor> factors;
  protected Scope scope;

  public GaussianModelBuilderImpl(Scope scope) {
    this.factors = new HashSet<GaussianFactor>();
    this.scope = scope;
  }

  public GaussianFactorBuilder<GaussianModelBuilder> factor() {
    return new GaussianFactorBuilderImpl(this, scope);
  }

  public void addFactor(GaussianFactor factor) {
    factors.add(factor);
  }

  public GraphicalModel<GaussianFactor> build() {
    // TODO: validate model here
    GraphicalModel<GaussianFactor> model = new GraphicalModel<GaussianFactor>(scope, factors);
    return model;
  }
}
