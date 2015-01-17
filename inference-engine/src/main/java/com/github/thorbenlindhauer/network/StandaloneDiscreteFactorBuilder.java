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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public class StandaloneDiscreteFactorBuilder implements DiscreteFactorBuilder<DiscreteFactor> {

  protected Map<String, DiscreteVariable> variables;

  protected Set<DiscreteVariable> currentVariables;

  public StandaloneDiscreteFactorBuilder() {
    this.variables = new HashMap<String, DiscreteVariable>();
  }

  @Override
  public DiscreteFactorBuilder<DiscreteFactor> scope(String... variableIds) {
    currentVariables = new HashSet<DiscreteVariable>();

    for (String variableId : variableIds) {
      DiscreteVariable variable = variables.get(variableId);

      if (variable == null) {
        throw new ModelStructureException("Variable " + variableId + " not defined for this builder");
      }

      currentVariables.add(variable);
    }

    return this;
  }

  @Override
  public DiscreteFactor basedOnTable(double[] table) {
    if (currentVariables == null) {
      throw new ModelStructureException("Cannot build factor without variables");
    }

    DiscreteFactor factor = new TableBasedDiscreteFactor(new Scope(currentVariables), table);
    currentVariables = null;

    return factor;
  }

  public static StandaloneDiscreteFactorBuilder withVariables(DiscreteVariable... variables) {
    StandaloneDiscreteFactorBuilder builder = new StandaloneDiscreteFactorBuilder();

    for (DiscreteVariable variable : variables) {
      builder.variables.put(variable.getId(), variable);
    }

    return builder;
  }
}
