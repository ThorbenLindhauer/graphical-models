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
package com.github.thorbenlindhauer.factor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.FactorOperationException;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.IndexCoder;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Factors are immutable.
 *
 * @author Thorben
 */
public class TableBasedDiscreteFactor implements DiscreteFactor {

  protected Scope variables;
  protected double[] values;

  public TableBasedDiscreteFactor(Scope variables, double[] values) {
    if (variables == null) {
      throw new ModelStructureException("Variables cannot be null");
    }

    this.variables = variables;
    this.values = values;
  }


  // TODO: think about making this a varargs method
  public TableBasedDiscreteFactor product(DiscreteFactor other) {
    Set<DiscreteVariable> newVars = new HashSet<DiscreteVariable>();
    newVars.addAll(this.variables.getVariables());
    newVars.addAll(other.getVariables().getVariables());

    Scope newVariables = new Scope(newVars);

    double[] newValues = new double[newVariables.getNumDistinctValues()];
    IndexCoder indexCoder = newVariables.getIndexCoder();
    int[] newCardinalities = indexCoder.getCardinalities();

    IndexCoder thisIndexCoder = variables.getIndexCoder();
    int[] thisVariableMapping = newVariables.createMapping(variables);
    int[] thisStrides = thisIndexCoder.getStrides();

    IndexCoder otherIndexCoder = other.getVariables().getIndexCoder();
    int[] otherVariableMapping = newVariables.createMapping(other.getVariables());
    int[] otherStrides = otherIndexCoder.getStrides();

    int[] assignment = new int[newVariables.size()];
    int thisIndex = 0;
    int otherIndex = 0;

    for (int i = 0; i < newValues.length; i++) {
      newValues[i] = values[thisIndex] * other.getValueAtIndex(otherIndex);

      for (int j = 0; j < newVariables.size(); j++) {
        assignment[j] = assignment[j] + 1;
        if (assignment[j] == newCardinalities[j]) {
          // TODO: probably expand thisStrides with thisVariablesMapping
          // before the loops such that the double indexing is not necessary
          assignment[j] = 0;
          if (thisVariableMapping[j] >= 0) {
            thisIndex -= (newCardinalities[j] - 1) * thisStrides[thisVariableMapping[j]];
          }

          if (otherVariableMapping[j] >= 0) {
            otherIndex -= (newCardinalities[j] - 1) * otherStrides[otherVariableMapping[j]];
          }
        } else {
          if (thisVariableMapping[j] >= 0) {
            thisIndex += thisStrides[thisVariableMapping[j]];
          }

          if (otherVariableMapping[j] >= 0) {
            otherIndex += otherStrides[otherVariableMapping[j]];
          }

          break;
        }
      }
    }


    TableBasedDiscreteFactor newFactor = new TableBasedDiscreteFactor(newVariables, newValues);
    return newFactor;
  }

  // TODO: good candidate for in-place computation
  public TableBasedDiscreteFactor division(DiscreteFactor other) {
    if (!variables.contains(other.getVariables().getVariableIds())) {
      throw new FactorOperationException("Divisor scope " + other.getVariables() + " is not a subset of" +
      		" this factor's scope " + variables);
    }

    Scope newVariables = variables;

    double[] newValues = Arrays.copyOf(values, values.length);

    int[] assignment = new int[newVariables.size()];
    int otherIndex = 0;

    IndexCoder indexCoder = newVariables.getIndexCoder();
    int[] cardinalities = indexCoder.getCardinalities();

    IndexCoder otherIndexCoder = other.getVariables().getIndexCoder();
    int[] otherVariableMapping = newVariables.createMapping(other.getVariables());
    int[] otherStrides = otherIndexCoder.getStrides();

    for (int i = 0; i < newValues.length; i++) {
      double otherValue = other.getValueAtIndex(otherIndex);

      if (otherValue == 0) {
        if (newValues[i] != 0) {
          throw new FactorOperationException("Invalid division operation for assignment " + assignment
              + ": " + newValues[i] + " / " + otherValue);
        }
        // no else branch: if newValues[i] == 0, then it is not changed

      } else {
        newValues[i] = newValues[i] / other.getValueAtIndex(otherIndex);
      }

      for (int j = 0; j < newVariables.size(); j++) {
        assignment[j] = assignment[j] + 1;
        if (assignment[j] == cardinalities[j]) {
          assignment[j] = 0;

          if (otherVariableMapping[j] >= 0) {
            otherIndex -= (cardinalities[j] - 1) * otherStrides[otherVariableMapping[j]];
          }
        } else {
          if (otherVariableMapping[j] >= 0) {
            otherIndex += otherStrides[otherVariableMapping[j]];
          }

          break;
        }
      }
    }

    TableBasedDiscreteFactor newFactor = new TableBasedDiscreteFactor(newVariables, newValues);
    return newFactor;
  }

  public TableBasedDiscreteFactor marginal(Scope scope) {
    Scope newScope = variables.intersect(scope);

    if (scope.contains(variables)) {
      return this;
    }

    double[] newValues = new double[newScope.getNumDistinctValues()];
    int newValuesIdx = 0;
    int[] newStrides = newScope.getIndexCoder().getStrides();
    int[] mapping = variables.createMapping(newScope);

    int[] thisCardinalities = variables.getIndexCoder().getCardinalities();
    int[] thisAssignment = new int[variables.size()];
    for (int i = 0; i < values.length; i++) {
      newValues[newValuesIdx] = newValues[newValuesIdx] + values[i];

      for (int j = 0; j < variables.size(); j++) {
        thisAssignment[j] = thisAssignment[j] + 1;
        if (thisAssignment[j] == thisCardinalities[j]) {
          thisAssignment[j] = 0;

          if (mapping[j] >= 0) {
            newValuesIdx -= (thisCardinalities[j] - 1) * newStrides[mapping[j]];
          }

        } else {
          if (mapping[j] >= 0) {
            newValuesIdx += newStrides[mapping[j]];
          }

          break;
        }

      }
    }


    return new TableBasedDiscreteFactor(newScope, newValues);
  }

  // TODO: consider implementing this as a view on the original factor
  public TableBasedDiscreteFactor observation(Scope scope, int[] observedValues) {
    if (scope.getVariables().size() != observedValues.length) {
      throw new ModelStructureException("Observed variables and values do not match");
    }

    if (variables.intersect(scope).isEmpty()) {
      return this;
    }


    double[] newValues = new double[values.length];
    int[] mapping = variables.createMapping(scope);

    for (int i = 0; i < values.length; i++) {
      // TODO: improve this, potential for precomputation before the loop
      boolean matches = true;

      for (int j = 0; j < mapping.length; j++) {

        if (mapping[j] >= 0) {
          int assignmentValue = observedValues[mapping[j]];
          int thisAssignmentValue = variables.getIndexCoder().getAssignmentAtPositionForIndex(i, j);

          if (assignmentValue != thisAssignmentValue) {
            matches = false;
            break;
          }
        }
      }

      if (matches) {
        newValues[i] = values[i];
      }
    }

    TableBasedDiscreteFactor newFactor = new TableBasedDiscreteFactor(variables, newValues);

    return newFactor;
  }

  public Scope getVariables() {
    return variables;
  }

  public double[] getValues() {
    return values;
  }

  public double getValueForAssignment(int[] assignment) {
    int index = variables.getIndexCoder().getIndexForAssignment(assignment);
    return getValueAtIndex(index);
  }

  public double getValueAtIndex(int index) {
    return values[index];
  }

  public TableBasedDiscreteFactor normalize() {
    return normalizeValuesBy(sumValues());
  }

  protected double sumValues() {
    double valueSum = 0.0d;

    for (double value : values) {
      valueSum += value;
    }

    return valueSum;
  }

  protected TableBasedDiscreteFactor normalizeValuesBy(double normalizationConstant) {
    double[] newValues = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      if (values[i] != 0) {
        newValues[i] = values[i] / normalizationConstant;
      }
    }

    return new TableBasedDiscreteFactor(variables, newValues);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("Scope: ");
    sb.append(variables.toString());

    return sb.toString();
  }

}
