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

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.CanonicalGaussianFactor;
import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.variable.ContinuousVariable;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

/**
 * @author Thorben
 *
 */
public class StandaloneGaussiaFactorBuilder implements GaussianFactorBuilder<GaussianFactor> {

  protected Map<String, Variable> variables;

  protected Set<Variable> currentVariables;

  public StandaloneGaussiaFactorBuilder() {
    this.variables = new HashMap<String, Variable>();
  }

  @Override
  public GaussianFactorBuilder<GaussianFactor> scope(String... variableIds) {
    currentVariables = new HashSet<Variable>();

    for (String variableId : variableIds) {
      Variable variable = variables.get(variableId);

      if (variable == null) {
        throw new ModelStructureException("Variable " + variableId + " not defined for this builder");
      }

      currentVariables.add(variable);
    }

    return this;
  }

  @Override
  public GaussianMomentFormBuilder<GaussianFactor> momentForm() {
    return new GaussianMomentFormBuilderImpl();
  }

  @Override
  public GaussianConditionalBuilder<GaussianFactor> conditional() {
    return new GaussianConditionalBuilderImpl();
  }

  public static StandaloneGaussiaFactorBuilder withVariables(ContinuousVariable... variables) {
    StandaloneGaussiaFactorBuilder builder = new StandaloneGaussiaFactorBuilder();

    for (ContinuousVariable variable : variables) {
      builder.variables.put(variable.getId(), variable);
    }

    return builder;
  }

  public class GaussianMomentFormBuilderImpl implements GaussianMomentFormBuilder<GaussianFactor> {


    @Override
    public GaussianFactor parameters(RealVector meanVector, RealMatrix covarianceMatrix) {

      Scope scope = new Scope(currentVariables);
      return CanonicalGaussianFactor.fromMomentForm(scope, meanVector, covarianceMatrix);
    }

  }

  public class GaussianConditionalBuilderImpl implements GaussianConditionalBuilder<GaussianFactor> {

    protected Scope conditioningScope;

    @Override
    public GaussianConditionalBuilder<GaussianFactor> conditioningScope(String... variableIds) {
      Set<Variable> conditioningVariables = new HashSet<Variable>();

      for (String variableId : variableIds) {
        Variable variable = variables.get(variableId);

        if (variable == null) {
          throw new ModelStructureException("Variable " + variableId + " not defined for this builder");
        }

        conditioningVariables.add(variable);
      }

      conditioningScope = new Scope(conditioningVariables);

      return this;
    }

    @Override
    public GaussianFactor parameters(RealVector meanVector, RealMatrix covarianceMatrix, RealMatrix weightMatrix) {

      Scope scope = new Scope(currentVariables);
      return CanonicalGaussianFactor.fromConditionalForm(scope, conditioningScope, meanVector, covarianceMatrix, weightMatrix);
    }


  }


}
