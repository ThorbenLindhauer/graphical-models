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

import com.github.thorbenlindhauer.learning.DistributionStructureException;
import com.github.thorbenlindhauer.learning.distribution.DirichletDistribution;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Manages priors for a single discrete variable conditioned on any number of discrete variables
 *
 * @author Thorben
 */
public class ConditionalDiscreteDistributionPrior {

  protected Scope scope;
  protected Scope describedScope;
  protected Scope conditioningScope;
  protected DirichletDistribution[] priors;
  protected DirichletPriorInitializer initializer;

  public ConditionalDiscreteDistributionPrior(Scope distributionScope, Scope conditioningScope) {
    this(distributionScope, conditioningScope, new UniformDirichletPriorInitializer());
  }

  public ConditionalDiscreteDistributionPrior(Scope distributionScope, Scope conditioningScope, DirichletPriorInitializer initialier) {
    if (!distributionScope.contains(conditioningScope)) {
      throw new DistributionStructureException("Conditioning scope " + conditioningScope +
          " must be subset of scope " + distributionScope);
    }
    if (!distributionScope.getContinuousVariables().isEmpty()) {
      throw new DistributionStructureException("Dirichlet Prior can only be used with discrete variables");
    }

    this.scope = distributionScope;
    this.conditioningScope = conditioningScope;
    this.describedScope = distributionScope.reduceBy(conditioningScope);
    this.initializer = initialier;

    if (describedScope.size() != 1) {
      throw new DistributionStructureException("Prior can describe exactly one variable");
    }

    initialize();
  }

  protected void initialize() {
    priors = new DirichletDistribution[conditioningScope.getNumDistinctValues()];

    for (int i = 0; i < priors.length; i++) {
      DirichletDistribution prior = new DirichletDistribution(describedScope.getNumDistinctValues());
      initializer.initialize(prior, describedScope, conditioningScope, conditioningScope.getIndexCoder().getAssignmentForIndex(i));
      priors[i] = prior;
    }

    // TODO prior initialization
  }
  
  public DirichletDistribution[] getPriors() {
    return priors;
  }


  public void submitEvidence(int[] conditioningAssignment, int[] observations) {
    if (conditioningAssignment.length != conditioningScope.size()) {
      throw new DistributionStructureException("Conditioning assignment must span " + conditioningScope.size() + " variables");
    }

    if (observations.length != describedScope.getNumDistinctValues()) {
      throw new DistributionStructureException("Must submit  " + describedScope.getNumDistinctValues() + " observations; was " + observations.length);
    }

    int priorIndex = conditioningScope.getIndexCoder().getIndexForAssignment(conditioningAssignment);
    DirichletDistribution prior = priors[priorIndex];
    prior.submitEvidence(observations);

  }

  public void submitEvidence(int[] conditioningAssignment, int observation) {
    int[] observationVector = new int[describedScope.getNumDistinctValues()];
    observationVector[observation] = 1;
    submitEvidence(conditioningAssignment, observationVector);
  }

  /**
   * @return all prior expectations encoded in the scheme as defined by the scope's index coder
   */
  public double[] toCanonicalValueVector() {
    double[] values = new double[scope.getNumDistinctValues()];

    int describedVariableIndex = -1;
    for (int i = 0; i < scope.getVariableIds().length; i++) {
      if (scope.getVariableId(i).equals(describedScope.getVariableId(0))) {
        describedVariableIndex = i;
        break;
      }
    }

    // TODO: improvement potential with solving via strides and index updates
    for (int i = 0; i < priors.length; i++) {
      DirichletDistribution prior = priors[i];
      double[] priorExpectation = prior.getExpectation();
      int[] conditioningAssignment = conditioningScope.getIndexCoder().getAssignmentForIndex(i);

      for (int j = 0; j < priorExpectation.length; j++) {

        int[] fullAssignment = new int[scope.size()];
        int conditioningIndex = 0;
        for (int k = 0; k < fullAssignment.length; k++) {
          if (k != describedVariableIndex) {
            fullAssignment[k] = conditioningAssignment[conditioningIndex];
            conditioningIndex++;
          } else {
            fullAssignment[k] = j;
          }
        }

        int fullIndex = scope.getIndexCoder().getIndexForAssignment(fullAssignment);
        values[fullIndex] = priorExpectation[j];
      }
    }

    return values;
  }
  
  public Scope getDescribedScope() {
    return describedScope;
  }
  
  public Scope getScope() {
    return scope;
  }
  
  public Scope getConditioningScope() {
    return conditioningScope;
  }

}
