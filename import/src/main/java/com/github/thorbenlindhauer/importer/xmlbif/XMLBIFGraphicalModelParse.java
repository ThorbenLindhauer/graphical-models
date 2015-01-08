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
package com.github.thorbenlindhauer.importer.xmlbif;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

public class XMLBIFGraphicalModelParse {

  protected Map<String, DiscreteVariable> scope;
  protected Set<DiscreteFactor> factors;

  public XMLBIFGraphicalModelParse() {
    this.scope = new HashMap<String, DiscreteVariable>();
    this.factors = new HashSet<DiscreteFactor>();
  }

  public void addVariable(DiscreteVariable variable) {
    this.scope.put(variable.getId(), variable);
  }

  public void addFactor(DiscreteFactor factor) {
    this.factors.add(factor);
  }

  public DiscreteVariable getVariable(String id) {
    return scope.get(id);
  }

  public Collection<DiscreteVariable> getVariables() {
    return scope.values();
  }

  public Set<DiscreteFactor> getFactors() {
    return factors;
  }

  public Scope scopeFor(String... variableIds) {
    Set<Variable> variables = new HashSet<Variable>();

    for (String variableId : variableIds) {
      variables.add(scope.get(variableId));
    }

    return new Scope(variables);
  }
}
