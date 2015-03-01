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
package com.github.thorbenlindhauer.learning.distribution;

import com.github.thorbenlindhauer.learning.DistributionStructureException;


/**
 * @author Thorben
 *
 */
public class DirichletDistribution {

  double[] parameters;

  public DirichletDistribution(int dimension) {
    parameters = new double[dimension];
  }

  public DirichletDistribution(double[] parameters) {
    this.parameters = parameters;
  }

  public void setParameter(int dimension, double value) {
    if (dimension >= parameters.length) {
      throw new DistributionStructureException("Cannot set value for dimension " + dimension +
          ". Distribution has " + parameters.length + " dimensions.");
    }
    if (value < 0) {
      throw new DistributionStructureException("Cannot set negative parameter value");
    }

    this.parameters[dimension] = value;
  }

  public void submitEvidence(int dimension, int value) {
    if (dimension >= parameters.length) {
      throw new DistributionStructureException("Cannot set value for dimension " + dimension +
          ". Distribution has " + parameters.length + " dimensions.");
    }
    if (value < 0) {
      throw new DistributionStructureException("Cannot set negative parameter value");
    }

    this.parameters[dimension] = this.parameters[dimension] + value;
  }

  public void submitEvidence(int[] values) {
    if (values.length != parameters.length) {
      throw new DistributionStructureException("Evidence must have " + parameters.length + " dimensions");
    }

    for (int i = 0; i < values.length; i++) {
      parameters[i] = parameters[i] + values[i];
    }
  }

  public double[] getParameters() {
    return parameters;
  }

  public double[] getExpectation() {
    double parameterSum = 0;
    for (double parameter : parameters) {
      parameterSum += parameter;
    }

    double[] expectation = new double[parameters.length];

    if (parameterSum > 0) {
      for (int i = 0; i < parameters.length; i++) {
        expectation[i] = (double) parameters[i] / (double) parameterSum;
      }
    }

    return expectation;
  }


}
