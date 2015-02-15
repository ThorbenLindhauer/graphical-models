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

import java.util.Set;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.CanonicalGaussianFactor;
import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

/**
 * @author Thorben
 *
 */
public class GaussianFactorBuilderImpl extends AbstractFactorBuilderImpl<GaussianFactorBuilder<GaussianModelBuilder>>
  implements GaussianFactorBuilder<GaussianModelBuilder> {

  protected GaussianModelBuilderImpl modelBuilder;

  public GaussianFactorBuilderImpl(GaussianModelBuilderImpl modelBuilder, Scope scope) {
    super(scope);
    this.modelBuilder = modelBuilder;
  }

  @Override
  public GaussianMomentFormBuilder<GaussianModelBuilder> momentForm() {
    return new GaussianMomentFormBuilderImpl();
  }

  @Override
  public GaussianConditionalBuilder<GaussianModelBuilder> conditional() {
    return new GaussianConditionalBuilderImpl();
  }

  public class GaussianMomentFormBuilderImpl implements GaussianMomentFormBuilder<GaussianModelBuilder> {


    @Override
    public GaussianModelBuilder parameters(RealVector meanVector, RealMatrix covarianceMatrix) {

      Scope scope = new Scope(factorVariables);
      GaussianFactor factor = CanonicalGaussianFactor.fromMomentForm(scope, meanVector, covarianceMatrix);
      modelBuilder.addFactor(factor);

      return modelBuilder;
    }

  }

  public class GaussianConditionalBuilderImpl implements GaussianConditionalBuilder<GaussianModelBuilder> {

    protected Scope conditioningScope;

    @Override
    public GaussianConditionalBuilder<GaussianModelBuilder> conditioningScope(String... variableIds) {
      Set<Variable> variables = determineVariables(variableIds);
      conditioningScope = new Scope(variables);

      return this;
    }

    @Override
    public GaussianModelBuilder parameters(RealVector meanVector, RealMatrix covarianceMatrix, RealMatrix weightMatrix) {
      Scope scope = new Scope(factorVariables);

      if (conditioningScope == null) {
        throw new ModelStructureException("Conditional Linear Gaussian over scope " + scope + " has no conditioning scope");
      }

      GaussianFactor factor = CanonicalGaussianFactor.fromConditionalForm(scope, conditioningScope, meanVector, covarianceMatrix, weightMatrix);
      modelBuilder.addFactor(factor);

      return modelBuilder;
    }


  }


}
