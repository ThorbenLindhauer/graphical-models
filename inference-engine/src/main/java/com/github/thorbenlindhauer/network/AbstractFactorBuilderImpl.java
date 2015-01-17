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

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

/**
 * @author Thorben
 *
 */
public abstract class AbstractFactorBuilderImpl<T extends FactorBuilder<T>> implements FactorBuilder<T> {

  protected Scope graphScope;
  protected Set<Variable> factorVariables;

  public AbstractFactorBuilderImpl(Scope scope) {
    this.graphScope = scope;
    this.factorVariables = new HashSet<Variable>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T scope(String... variableIds) {
    Set<Variable> variables = determineVariables(variableIds);
    factorVariables.addAll(variables);

    return (T) this;
  }

  protected Set<Variable> determineVariables(String... variableIds) {
    Set<Variable> variables = new HashSet<Variable>();

    for (String variableId : variableIds) {
      Variable variable = graphScope.getVariable(variableId);

      if (variable == null) {
        throw new ModelStructureException("Variable " + variableId + " not defined in scope of graph.");
      }

      variables.add(variable);
    }

    return variables;
  }
}
