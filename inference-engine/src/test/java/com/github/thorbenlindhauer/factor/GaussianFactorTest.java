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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.exception.FactorOperationException;
import com.github.thorbenlindhauer.test.util.TestConstants;
import com.github.thorbenlindhauer.variable.ContinuousVariable;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

public class GaussianFactorTest {

  protected GaussianFactor abFactor;
  protected GaussianFactor acFactor;
  protected GaussianFactor abcFactor;

  @Before
  public void setUp() {
    acFactor = newFactor(
        newScope(new ContinuousVariable("A"), new ContinuousVariable("C")),
        new double[][]{
          {1.0d, 2.0d},
          {3.0d, 1.0d}
        },
        new double[]{5.0d, 6.0d},
        5.5d);

    abFactor = newFactor(
        newScope(new ContinuousVariable("A"), new ContinuousVariable("B")),
        new double[][]{
          {5.0d, 1.0d},
          {1.0d, 2.0d}
        },
        new double[]{3.0d, 2.0d},
        2.2d);

    abcFactor = newFactor(
        newScope(new ContinuousVariable("A"), new ContinuousVariable("B"), new ContinuousVariable("C")),
        new double[][]{
          {3.0d, 4.0d, 6.0d},
          {3.0d, 6.0d, 7.0d},
          {10.0d, 3.0d, 5.5d}
        },
        new double[]{3.0d, 2.0d, 1.5d},
        8.5d);
  }


  @Test
  public void testFactorInitialization() {
    Scope scope = newScope(new ContinuousVariable("A"), new ContinuousVariable("B"), new ContinuousVariable("C"));

    RealMatrix covarianceMatrix = new Array2DRowRealMatrix(new double[][] {
        {1.0d, 2.0d, 3.0d},
        {4.0d, 5.0d, 6.0d},
        {7.0d, 8.0d, 10.0d}
    });

    RealVector meanVector = new ArrayRealVector(new double[] {1.0d, 4.0d, 7.0d});

    // when
    GaussianFactor factor = CanonicalGaussianFactor.fromMomentForm(scope, covarianceMatrix, meanVector);

    // then
    RealMatrix returnedCovarianceMatrix = factor.getCovarianceMatrix();
    assertThat(returnedCovarianceMatrix.getColumnDimension()).isEqualTo(3);
    assertThat(returnedCovarianceMatrix.getRowDimension()).isEqualTo(3);

    double[] row = returnedCovarianceMatrix.getRowVector(0).toArray();
    assertThat(row[0]).isEqualTo(1.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(2.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    row = returnedCovarianceMatrix.getRowVector(1).toArray();
    assertThat(row[0]).isEqualTo(4.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(5.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(6.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    row = returnedCovarianceMatrix.getRowVector(2).toArray();
    assertThat(row[0]).isEqualTo(7.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(8.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(10.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    double[] returnedMeanVector = factor.getMeanVector().toArray();
    assertThat(returnedMeanVector[0]).isEqualTo(1.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(returnedMeanVector[1]).isEqualTo(4.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(returnedMeanVector[2]).isEqualTo(7.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  @Test
  public void testInvalidFactorSerialization() {
    Scope scope = newScope(new DiscreteVariable("A", 5), new ContinuousVariable("B"));

    RealMatrix covarianceMatrix = new Array2DRowRealMatrix(new double[][] {
        {1.0d, 2.0d},
        {4.0d, 7.0d}
    });

    RealVector meanVector = new ArrayRealVector(new double[] {1.0d, 4.0d});

    try {
      CanonicalGaussianFactor.fromMomentForm(scope, covarianceMatrix, meanVector);
      fail("should not suceed as a gaussian factor cannot be defined over a discrete variable");
    } catch (Exception e) {
      // happy path
    }
  }

  // TODO: test validation of variables (i.e. that continuous variables match the matrix and vector)
  // same for discrete factors;

  @Test
  public void testFactorProduct() {
    // when
    GaussianFactor product = acFactor.product(abFactor);

    // then
    Collection<Variable> newVariables = product.getVariables().getVariables();
    assertThat(newVariables).hasSize(3);
    assertThat(newVariables).containsAll(acFactor.getVariables().getVariables());
    assertThat(newVariables).containsAll(abFactor.getVariables().getVariables());

    // precision matrix
    RealMatrix precisionMatrix = product.getPrecisionMatrix();
    assertThat(precisionMatrix.isSquare()).isTrue();
    assertThat(precisionMatrix.getColumnDimension()).isEqualTo(3);

    double[] row = precisionMatrix.getRowVector(0).toArray();
    assertThat(row[0]).isEqualTo(6.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(1.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(2.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    row = precisionMatrix.getRowVector(1).toArray();
    assertThat(row[0]).isEqualTo(1.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(2.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(0.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    row = precisionMatrix.getRowVector(2).toArray();
    assertThat(row[0]).isEqualTo(3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(0.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(1.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    // scaled mean vector
    RealVector scaledMeanVector = product.getScaledMeanVector();
    assertThat(scaledMeanVector.getDimension()).isEqualTo(3);

    double[] meanVectorValues = scaledMeanVector.toArray();
    assertThat(meanVectorValues[0]).isEqualTo(8.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(meanVectorValues[1]).isEqualTo(2.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(meanVectorValues[2]).isEqualTo(6.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    assertThat(product.getNormalizationConstant()).isEqualTo(7.7d, TestConstants.DOUBLE_VALUE_TOLERANCE);

  }

  protected GaussianFactor newFactor(Scope scope, double[][] precisionMatrix, double[] scaledMeanVector, double normalizationConstant) {
    return new CanonicalGaussianFactor(scope, new Array2DRowRealMatrix(precisionMatrix),
        new ArrayRealVector(scaledMeanVector), normalizationConstant);
  }

  @Test
  public void testFactorProductOfIndependentFactors() {
    // TODO: implement
  }

  @Test
  public void testFactorProductWithConstantValueFactor() {
    // TODO: implement
  }

  @Test
  public void testFactorMarginalCase1() {
    Scope variables = newScope(new ContinuousVariable("A"), new ContinuousVariable("C"));

    GaussianFactor acMarginal = abcFactor.marginal(variables);

    // then
    Collection<Variable> newVariables = acMarginal.getVariables().getVariables();
    assertThat(newVariables).hasSize(2);
    assertThat(newVariables).containsAll(variables.getVariables());

    // precision matrix: K_xx - K_xy * K_yy^(-1) * K_yx
    RealMatrix precisionMatrix = acMarginal.getPrecisionMatrix();
    assertThat(precisionMatrix.isSquare()).isTrue();
    assertThat(precisionMatrix.getColumnDimension()).isEqualTo(2);

    double[] row = precisionMatrix.getRowVector(0).toArray();
    assertThat(row[0]).isEqualTo(1, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(4.0d / 3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    row = precisionMatrix.getRowVector(1).toArray();
    assertThat(row[0]).isEqualTo(8.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(2.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    // scaled mean vector: h_x - K_xy * K_yy^(-1) * h_y
    RealVector scaledMeanVector = acMarginal.getScaledMeanVector();
    assertThat(scaledMeanVector.getDimension()).isEqualTo(2);

    double[] meanValues = scaledMeanVector.toArray();
    assertThat(meanValues[0]).isEqualTo(5.0d / 3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(meanValues[1]).isEqualTo(0.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    // normalization constant: g + 0.5 * (log( det( 2 * PI * K_yy^(-1))) + h_y * K_yy^(-1) * h_y)
    assertThat(acMarginal.getNormalizationConstant()).isEqualTo(8.856392131, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  @Test
  public void testFactorMarginalCase2() {
    Scope variables = newScope(new ContinuousVariable("A"));

    GaussianFactor aMarginal = abcFactor.marginal(variables);

//    new double[][]{
//        {3.0d, 4.0d, 6.0d},
//        {3.0d, 6.0d, 7.0d},
//        {10.0d, 3.0d, 5.5d}
//      },
//      new double[]{3.0d, 2.0d, 1.5d},
//      8.5d);

    // then
    Collection<Variable> newVariables = aMarginal.getVariables().getVariables();
    assertThat(newVariables).hasSize(1);
    assertThat(newVariables).contains(new ContinuousVariable("A"));

    // precision matrix: K_xx - K_xy * K_yy^(-1) * K_yx
    RealMatrix precisionMatrix = aMarginal.getPrecisionMatrix();
    assertThat(precisionMatrix.isSquare()).isTrue();
    assertThat(precisionMatrix.getColumnDimension()).isEqualTo(1);

    double precision = precisionMatrix.getRowVector(0).toArray()[0];
    assertThat(precision).isEqualTo(-(14.0d / 3.0d), TestConstants.DOUBLE_VALUE_TOLERANCE);

    // scaled mean vector: h_x - K_xy * K_yy^(-1) * h_y
    RealVector scaledMeanVector = aMarginal.getScaledMeanVector();
    assertThat(scaledMeanVector.getDimension()).isEqualTo(1);

    double meanValue = scaledMeanVector.toArray()[0];
    assertThat(meanValue).isEqualTo(4.0d / 3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    // normalization constant: g + 0.5 * (log( det( 2 * PI * K_yy^(-1))) + h_y * K_yy^(-1) * h_y)
    assertThat(aMarginal.getNormalizationConstant()).isEqualTo(9.324590408d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  // TODO: test for case when matrix YY is not positive definite => should throw exception then

  @Test
  public void testValueObservation() {
    // TODO: implement
  }

  @Test
  public void testFactorDivision() {
    GaussianFactor quotient = abcFactor.division(abFactor);

    // then
    Collection<Variable> newVariables = quotient.getVariables().getVariables();
    assertThat(newVariables).hasSize(3);
    assertThat(newVariables).containsAll(abcFactor.getVariables().getVariables());
    assertThat(newVariables).containsAll(abFactor.getVariables().getVariables());

    // precision matrix
    RealMatrix precisionMatrix = quotient.getPrecisionMatrix();
    assertThat(precisionMatrix.isSquare()).isTrue();
    assertThat(precisionMatrix.getColumnDimension()).isEqualTo(3);

    double[] row = precisionMatrix.getRowVector(0).toArray();
    assertThat(row[0]).isEqualTo(-2.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(6.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    row = precisionMatrix.getRowVector(1).toArray();
    assertThat(row[0]).isEqualTo(2.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(4.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(7.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    row = precisionMatrix.getRowVector(2).toArray();
    assertThat(row[0]).isEqualTo(10.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[1]).isEqualTo(3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(row[2]).isEqualTo(5.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    // scaled mean vector
    RealVector scaledMeanVector = quotient.getScaledMeanVector();
    assertThat(scaledMeanVector.getDimension()).isEqualTo(3);

    double[] meanVectorValues = scaledMeanVector.toArray();
    assertThat(meanVectorValues[0]).isEqualTo(0.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(meanVectorValues[1]).isEqualTo(0.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(meanVectorValues[2]).isEqualTo(1.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    assertThat(quotient.getNormalizationConstant()).isEqualTo(6.3d, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }


  @Test
  public void testFactorDivisionMismatchingScopes() {
    try {
      abFactor.division(acFactor);
      fail("expected exception");
    } catch (FactorOperationException e) {
      // happy path
    }
  }

  protected Scope newScope(Variable... variables) {
    Set<Variable> variableArgs = new HashSet<Variable>();
    for (Variable variable : variables) {
      variableArgs.add(variable);
    }

    return new Scope(variableArgs);
  }
}
