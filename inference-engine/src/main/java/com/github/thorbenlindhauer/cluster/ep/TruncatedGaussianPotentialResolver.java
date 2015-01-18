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
package com.github.thorbenlindhauer.cluster.ep;

import java.util.Collections;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import com.github.thorbenlindhauer.exception.InferenceException;
import com.github.thorbenlindhauer.factor.CanonicalGaussianFactor;
import com.github.thorbenlindhauer.factor.FactorSet;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.variable.ContinuousVariable;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Implements expectation propagation for univariate truncated Gaussians.
 *
 * @author Thorben
 *
 */
public class TruncatedGaussianPotentialResolver implements ClusterPotentialResolver<GaussianFactor>{

  protected double lowerBound;
  protected double upperBound;
  protected ContinuousVariable predictionVariable;

  protected NormalDistribution standardNormal;

  public TruncatedGaussianPotentialResolver(ContinuousVariable predictionVariable, double lowerBound, double upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.predictionVariable = predictionVariable;
    this.standardNormal = new NormalDistribution();
  }

  @Override
  public FactorSet<GaussianFactor> project(FactorSet<GaussianFactor> additionalFactors, Scope projectionScope) {

    if (projectionScope.size() != 1 || !projectionScope.contains(predictionVariable.getId())) {
      throw new InferenceException("Can only project on variable " + predictionVariable.getId());
    }

    for (GaussianFactor factor : additionalFactors.getFactors()) {
      Scope factorScope = factor.getVariables();
      if (factorScope.size() != 1 || !factorScope.contains(predictionVariable.getId())) {
        throw new InferenceException("Can only project univariate gaussians over variable " + predictionVariable.getId());
      }
    }

    GaussianFactor jointFactor = FactorUtil.jointDistribution(additionalFactors.getFactors());

    double jointVariance = jointFactor.getCovarianceMatrix().getEntry(0, 0);
    double jointStandardDeviation = Math.sqrt(jointVariance);
    double jointMean = jointFactor.getMeanVector().getEntry(0);

    double adjustedLowerBound = lowerBound / jointStandardDeviation;
    double adjustedUpperBound = upperBound / jointStandardDeviation;
    double adjustedMean = jointMean / jointStandardDeviation;

    double vValue = vValue(adjustedMean, adjustedLowerBound, adjustedUpperBound);
    double wValue = wValue(vValue, adjustedMean, adjustedLowerBound, adjustedUpperBound);

    double truncatedMean = jointMean + (jointStandardDeviation * vValue);
    double truncatedVariance = jointVariance * (1 - wValue);

    GaussianFactor approximationFactor = CanonicalGaussianFactor.fromMomentForm(
        projectionScope,
        new ArrayRealVector(new double[]{ truncatedMean }),
        new Array2DRowRealMatrix(new double[]{ truncatedVariance }));

    return new FactorSet<GaussianFactor>(Collections.singleton(approximationFactor));
  }

  protected double vValue(double mean, double lowerBound, double upperBound) {
    double shiftedUpperBound = upperBound - mean;
    double shifterLowerBound = lowerBound - mean;

    return (standardNormal.density(shifterLowerBound) - standardNormal.density(shiftedUpperBound)) /
        (standardNormal.cumulativeProbability(shiftedUpperBound) - standardNormal.cumulativeProbability(shifterLowerBound));
  }

  protected double wValue(double vValue, double mean, double lowerBound, double upperBound) {
    double shiftedUpperBound = upperBound - mean;
    double shifterLowerBound = lowerBound - mean;

    return vValue * vValue
        + (shiftedUpperBound * standardNormal.density(shiftedUpperBound) - shifterLowerBound * standardNormal.density(shifterLowerBound)) /
        (standardNormal.cumulativeProbability(shiftedUpperBound) - standardNormal.cumulativeProbability(shifterLowerBound));
  }

}
