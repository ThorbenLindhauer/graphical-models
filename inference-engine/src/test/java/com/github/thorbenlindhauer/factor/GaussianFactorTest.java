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
import org.junit.Ignore;
import org.junit.Test;

import com.github.thorbenlindhauer.exception.FactorOperationException;
import com.github.thorbenlindhauer.network.StandaloneGaussiaFactorBuilder;
import com.github.thorbenlindhauer.test.util.TestConstants;
import com.github.thorbenlindhauer.variable.ContinuousVariable;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

public class GaussianFactorTest {

  protected GaussianFactor abFactor;
  protected GaussianFactor acFactor;
  protected GaussianFactor abcFactor;

  protected StandaloneGaussiaFactorBuilder factorBuilder;

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

    factorBuilder = StandaloneGaussiaFactorBuilder.withVariables(
        new ContinuousVariable("A"),
        new ContinuousVariable("B"),
        new ContinuousVariable("C"));
  }


  @Test
  public void testInitializationFromMomentForm() {
    Scope scope = newScope(new ContinuousVariable("A"), new ContinuousVariable("B"), new ContinuousVariable("C"));

    RealMatrix covarianceMatrix = new Array2DRowRealMatrix(new double[][] {
        {1.0d, 2.0d, 3.0d},
        {4.0d, 5.0d, 6.0d},
        {7.0d, 8.0d, 10.0d}
    });

    RealVector meanVector = new ArrayRealVector(new double[] {1.0d, 4.0d, 7.0d});

    // when
    GaussianFactor factor = CanonicalGaussianFactor.fromMomentForm(scope, meanVector, covarianceMatrix);

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
  public void testProbabilityForAssignment() {
    GaussianFactor oneVariableFactor = factorBuilder
        .scope("A")
        .momentForm()
        .parameters(
            new ArrayRealVector(new double[]{ 3.0d }),
            new Array2DRowRealMatrix(new double[]{ 2.0d }));

    assertThat(oneVariableFactor.getValueForAssignment(new double[]{ 2.5d })).isEqualTo(0.265004, TestConstants.DOUBLE_VALUE_TOLERANCE);

    GaussianFactor threeVariableFactor = factorBuilder
        .scope("A", "B", "C")
        .momentForm()
        .parameters(
            new ArrayRealVector(new double[]{ 2.0d, 3.0d, 4.0d }),
            new Array2DRowRealMatrix(new double[][]{
                { 1.0d, 0.4d, 0.5d},
                { 0.4d, 1.0d, 0},
                { 0.5d, 0, 1.0d}}));

    assertThat(threeVariableFactor.getValueForAssignment(new double[]{ 1.0d, 2.0d, 4.0d })).isEqualTo(0.0369539, TestConstants.DOUBLE_VALUE_TOLERANCE);
  }

  @Test
  public void testInitializationFromConditionalLinearGaussian() {
    GaussianFactor factor =
        factorBuilder
          .scope("A", "B")
          .conditional().conditioningScope("B")
          .parameters(new ArrayRealVector(new double[]{ 4.0d }), // mean of A
              new Array2DRowRealMatrix(new double[]{ 2.0d }),    // variance for A
              new Array2DRowRealMatrix(new double[]{ 5.0d }));   // weight of B

    // P(A = 3 | B = 1.5)
    assertThat(factor.getValueForAssignment(new double[]{ 10.0d, 1.5d })).isEqualTo(0.160733d, TestConstants.DOUBLE_VALUE_TOLERANCE);

  }

  // TODO: fix
  @Test
  @Ignore
  public void testConvolution() {
    GaussianFactor factor =
        factorBuilder
          .scope("A", "B", "C")
          .conditional().conditioningScope("B", "C")
          .parameters(new ArrayRealVector(new double[]{ 0.0d }), // mean of A
              new Array2DRowRealMatrix(new double[]{ 1.0d }),    // variance for A (allowed to be 0 in plain convolution)
              new Array2DRowRealMatrix(new double[][]{ {1.0d, 1.0d} }));   // weight of B and C

    GaussianFactor bFactor =
        factorBuilder.scope("B").momentForm().parameters(new ArrayRealVector(new double[]{ 2.5d }), new Array2DRowRealMatrix(new double[] { 0.8d }));

    GaussianFactor cFactor =
        factorBuilder.scope("C").momentForm().parameters(new ArrayRealVector(new double[]{ 1.5d }), new Array2DRowRealMatrix(new double[] { 1.3d }));

    GaussianFactor abcFactor = factor.product(bFactor).product(cFactor);
    GaussianFactor marginalFactor = abcFactor.marginal(factor.getVariables().reduceBy("B", "C"));

    assertThat(marginalFactor.getMeanVector().getEntry(0)).isEqualTo(2.5d + 1.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);
    assertThat(marginalFactor.getCovarianceMatrix().getEntry(0, 0)).isEqualTo(0.8d + 1.3d, TestConstants.DOUBLE_VALUE_TOLERANCE);

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
      CanonicalGaussianFactor.fromMomentForm(scope, meanVector, covarianceMatrix);
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
    GaussianFactor reducedVector = abcFactor.observation(newScope(new ContinuousVariable("A")), new double[]{ 2.5d });

    // then
    Collection<Variable> newVariables = reducedVector.getVariables().getVariables();
    assertThat(newVariables).hasSize(2);
    assertThat(newVariables).contains(new ContinuousVariable("B"), new ContinuousVariable("C"));

    //  B     C    A
    //6.0d, 7.0d, 3.0d
    //3.0d, 5.5d, 10.0d
    //4.0d, 6.0d, 3.0d
    //
    // X = {B, C}, Y = {A}

    // precision matrix: K_xx
    RealMatrix precisionMatrix = reducedVector.getPrecisionMatrix();
    assertThat(precisionMatrix.isSquare()).isTrue();
    assertThat(precisionMatrix.getColumnDimension()).isEqualTo(2);

    double precision = precisionMatrix.getEntry(0, 0);
    assertThat(precision).isEqualTo(6.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    precision = precisionMatrix.getEntry(0, 1);
    assertThat(precision).isEqualTo(7.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    precision = precisionMatrix.getEntry(1, 0);
    assertThat(precision).isEqualTo(3.0d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    precision = precisionMatrix.getEntry(1, 1);
    assertThat(precision).isEqualTo(5.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    // scaled mean vector: h_x - K_xy * y
    RealVector scaledMeanVector = reducedVector.getScaledMeanVector();
    assertThat(scaledMeanVector.getDimension()).isEqualTo(2);

    double meanValue = scaledMeanVector.getEntry(0);
    assertThat(meanValue).isEqualTo(- 5.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    meanValue = scaledMeanVector.getEntry(1);
    assertThat(meanValue).isEqualTo(- 23.5d, TestConstants.DOUBLE_VALUE_TOLERANCE);

    // normalization constant: g + h_y * y - 0.5 * (y * K_yy * y)
    //                         8.5 + 7.5 - 9,375
    assertThat(reducedVector.getNormalizationConstant()).isEqualTo(6.625d, TestConstants.DOUBLE_VALUE_TOLERANCE);
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
