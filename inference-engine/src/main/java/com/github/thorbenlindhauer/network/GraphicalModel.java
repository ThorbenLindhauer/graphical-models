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

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;

public class GraphicalModel {

  protected Set<DiscreteFactor> factors;
  protected Scope scope;

  public GraphicalModel(Scope scope, Set<DiscreteFactor> factors) {
    this.factors = factors;
    this.scope = scope;
  }

  public Set<DiscreteFactor> getFactors() {
    return new HashSet<DiscreteFactor>(factors);
  }

  public Scope getScope() {
    return scope;
  }

  public static ScopeBuilder create() {
    return new ScopeBuilderImpl();
  }
}
