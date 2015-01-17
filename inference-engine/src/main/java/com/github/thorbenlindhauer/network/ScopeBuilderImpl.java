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

import java.util.HashMap;
import java.util.Map;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

public class ScopeBuilderImpl implements ScopeBuilder {

  protected Map<String, DiscreteVariable> variables;

  public ScopeBuilderImpl() {
    this.variables = new HashMap<String, DiscreteVariable>();
  }

  public ScopeBuilder variable(String id, int cardinality) {
    DiscreteVariable variable = new DiscreteVariable(id, cardinality);
    variables.put(id, variable);
    return this;
  }

  public ModelBuilder<DiscreteFactor, DiscreteFactorBuilder<DiscreteModelBuilder>> discreteNetwork() {
    Scope scope = new Scope(variables.values());
    return new DiscreteModelBuilderImpl(scope);
  }

  public ModelBuilder<GaussianFactor, GaussianFactorBuilder<GaussianModelBuilder>> gaussianNetwork() {
    Scope scope = new Scope(variables.values());
    return new GaussianModelBuilderImpl(scope);
  }


}
