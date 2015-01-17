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

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorChangingVisitor;

import com.github.thorbenlindhauer.exception.FactorOperationException;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.math.MathUtil;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public class CanonicalGaussianFactor implements GaussianFactor {

  protected Scope scope;

  protected RealMatrix precisionMatrix;
  protected RealVector scaledMeanVector;

  protected double normalizationConstant;

  public CanonicalGaussianFactor(Scope scope, RealMatrix precisionMatrix, RealVector scaledMeanVector, double normalizationConstant) {
    if (!scope.getDiscreteVariables().isEmpty()) {
      throw new ModelStructureException("Cannot define a Gaussian factor with discrete variables " + scope.getDiscreteVariables());
    }

    this.scope = scope;
    this.precisionMatrix = precisionMatrix;
    this.scaledMeanVector = scaledMeanVector;
    this.normalizationConstant = normalizationConstant;
  }

  @Override
  public GaussianFactor product(GaussianFactor other) {
    Scope newScope = scope.union(other.getVariables());

    int newFactorSize = newScope.size();

    int[] thisMapping = newScope.createContinuousVariableMapping(scope);
    int[] otherMapping = newScope.createContinuousVariableMapping(other.getVariables());

    RealMatrix newPrecisionMatrix = new Array2DRowRealMatrix(newScope.size(), newScope.size());

    RealMatrix otherPrecisionMatrix = other.getPrecisionMatrix();

    for (int i = 0; i < newFactorSize; i++) {
      RealVector column = new ArrayRealVector(newFactorSize);
      if (thisMapping[i] >= 0) {
        column = column.add(padVector(precisionMatrix.getColumnVector(thisMapping[i]), newFactorSize, thisMapping));
      }

      if (otherMapping[i] >= 0) {
        column = column.add(padVector(otherPrecisionMatrix.getColumnVector(otherMapping[i]), newFactorSize, otherMapping));
      }
      newPrecisionMatrix.setColumnVector(i, column);
    }

    RealVector newScaledMeanVector = padVector(scaledMeanVector, newScope.size(), thisMapping);
    RealVector otherScaledMeanVector = other.getScaledMeanVector();
    newScaledMeanVector = newScaledMeanVector.add(padVector(otherScaledMeanVector, newFactorSize, otherMapping));

    double newNormalizationConstant = normalizationConstant + other.getNormalizationConstant();

    return new CanonicalGaussianFactor(newScope, newPrecisionMatrix, newScaledMeanVector, newNormalizationConstant);
  }

  /**
   * mapping must have the size of the new vector; maps a vector to a new size by applying the mapping of the positions
   * and fills the remaining places with 0 values
   */
  protected static RealVector padVector(final RealVector vector, int newSize, final int[] mapping) {
    final RealVector newVector = new ArrayRealVector(newSize);

    newVector.walkInOptimizedOrder(new RealVectorChangingVisitor() {

      @Override
      public double visit(int index, double value) {
        if (mapping[index] >= 0) {
          return vector.getEntry(mapping[index]);
        } else {
          return 0;
        }
      }

      @Override
      public void start(int dimension, int start, int end) {
      }

      @Override
      public double end() {
        return 0;
      }
    });
    return newVector;
  }

  @Override
  public GaussianFactor division(GaussianFactor other) {
    if (!scope.contains(other.getVariables().getVariableIds())) {
      throw new FactorOperationException("Divisor scope " + other.getVariables() + " is not a subset of" +
          " this factor's scope " + scope);
    }

    int[] otherMapping = scope.createContinuousVariableMapping(other.getVariables());

    RealMatrix newPrecisionMatrix = precisionMatrix.copy();

    RealMatrix otherPrecisionMatrix = other.getPrecisionMatrix();

    for (int i = 0; i < scope.size(); i++) {
      RealVector column = newPrecisionMatrix.getColumnVector(i);

      if (otherMapping[i] >= 0) {
        column = column.subtract(padVector(otherPrecisionMatrix.getColumnVector(otherMapping[i]), scope.size(), otherMapping));
        newPrecisionMatrix.setColumnVector(i, column);
      }
    }

    RealVector newScaledMeanVector = scaledMeanVector.copy();
    RealVector otherScaledMeanVector = other.getScaledMeanVector();
    newScaledMeanVector = newScaledMeanVector.subtract(padVector(otherScaledMeanVector, scope.size(), otherMapping));

    double newNormalizationConstant = normalizationConstant - other.getNormalizationConstant();

    return new CanonicalGaussianFactor(scope, newPrecisionMatrix, newScaledMeanVector, newNormalizationConstant);
  }

  @Override
  public GaussianFactor marginal(Scope marginalizationScope) {
    // the following assumes that the precision matrix (and mean vector) can be restructured as follows:
    // ( SUBMATRIX_XX SUBMATRIX_XY )
    // ( SUBMATRIX_YX SUBMATRIX_YY )
    // where X indicates the entries for the variables that are kept (i.e. in the scope argument) and
    // Y the variables that are marginalized out

    if (marginalizationScope.contains(scope)) {
      return this;
    }

    Scope newScope = scope.intersect(marginalizationScope);
    Scope scopeToMarginalize = scope.reduceBy(newScope);

    int[] xMapping = newScope.createContinuousVariableMapping(scope);
    RealMatrix xxMatrix = precisionMatrix.getSubMatrix(xMapping, xMapping);

    int[] yMapping = scopeToMarginalize.createContinuousVariableMapping(scope);
    RealMatrix yyMatrix = precisionMatrix.getSubMatrix(yMapping, yMapping);

    RealMatrix xyMatrix = precisionMatrix.getSubMatrix(xMapping, yMapping);
    RealMatrix yxMatrix = precisionMatrix.getSubMatrix(yMapping, xMapping);

    MathUtil yyUtil = new MathUtil(yyMatrix);
    RealMatrix yyInverse = yyUtil.invert();
    RealMatrix newPrecisionMatrix = xxMatrix.subtract(xyMatrix.multiply(yyInverse.multiply(yxMatrix)));

    RealVector xVector = getSubVector(scaledMeanVector, xMapping);
    RealVector yVector = getSubVector(scaledMeanVector, yMapping);

    RealVector newScaledMeanVector = xVector.subtract(xyMatrix.operate(yyInverse.operate(yVector)));

    MathUtil inverseUtil = new MathUtil(yyInverse.scalarMultiply(2.0d * Math.PI));
    double newNormalizationConstant = normalizationConstant +
        0.5d * (Math.log(inverseUtil.determinant()) + yVector.dotProduct(yyInverse.operate(yVector)));

    return new CanonicalGaussianFactor(newScope, newPrecisionMatrix, newScaledMeanVector, newNormalizationConstant);
  }

  protected RealVector getSubVector(RealVector vector, int[] entriesToKeep) {
    RealVector subVector = new ArrayRealVector(entriesToKeep.length);

    for (int i = 0; i < entriesToKeep.length; i++) {
      subVector.setEntry(i, vector.getEntry(entriesToKeep[i]));
    }

    return subVector;
  }

  @Override
  public GaussianFactor normalize() {

    // assuming the gaussian is always normalized by its normalization constant

    return this;
  }

  @Override
  public GaussianFactor invert() {
    // TODO implement

    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public Scope getVariables() {
    return scope;
  }

  @Override
  public RealMatrix getPrecisionMatrix() {
    return precisionMatrix;
  }

  @Override
  public RealVector getScaledMeanVector() {
    return scaledMeanVector;
  }

  @Override
  public double getNormalizationConstant() {
    return normalizationConstant;
  }

  @Override
  public RealMatrix getCovarianceMatrix() {
    MathUtil mathUtil = new MathUtil(precisionMatrix);
    return mathUtil.invert();
  }

  @Override
  public RealVector getMeanVector() {
    MathUtil mathUtil = new MathUtil(precisionMatrix);
    return mathUtil.invert().operate(scaledMeanVector);
  }

  @Override
  public double getValueForAssignment(double[] assignment) {
    RealVector assignmentVector = new ArrayRealVector(assignment);

    double exponent = - 0.5d * assignmentVector.dotProduct(precisionMatrix.operate(assignmentVector))
        + scaledMeanVector.dotProduct(assignmentVector) + normalizationConstant;

    return Math.exp(exponent);
  }

  public static CanonicalGaussianFactor fromMomentForm(Scope scope, RealVector meanVector, RealMatrix covarianceMatrix) {

    // TODO: perform cardinality checks etc.

    MathUtil mathUtil = new MathUtil(covarianceMatrix);
    RealMatrix precisionMatrix = mathUtil.invert();
    RealVector scaledMeanVector = precisionMatrix.operate(meanVector);
    int dimension = meanVector.getDimension();
    double normalizationConstant = - (0.5d * scaledMeanVector.dotProduct(meanVector)) -
        (Math.log(Math.pow(2.0d * Math.PI, (double) dimension / 2.0d) * Math.sqrt(mathUtil.determinant())));

    return new CanonicalGaussianFactor(scope, precisionMatrix, scaledMeanVector, normalizationConstant);
  }

  /**
   * Transforms a conditional linear gaussian (i.e. a Gaussian of the form N(x; a + B^T * Y, C) into canonical form.
   *
   * @param meanVector a
   * @param weightMatrix B
   * @param covarianceMatrix C
   */
  public static CanonicalGaussianFactor fromConditionalForm(Scope scope, Scope conditioningScope, RealVector meanVector,
    RealMatrix covarianceMatrix, RealMatrix weightMatrix) {

 // TODO: perform cardinality checks etc.

    // the following assumes that the resulting precision matrix (and mean vector) can be restructured as follows:
    // ( SUBMATRIX_XX SUBMATRIX_XY )
    // ( SUBMATRIX_YX SUBMATRIX_YY )
    // where X indicates variables that are part of the prediction scope and Y are variables being part of the conditioning scope

    // assuming
    //   meanVector: a
    //   covarianceMatrix: C
    //   weightMatrix: B


    // XX = C^-1
    // XY = -C^-1 * B
    // YX = -B^T * C^-1
    // YY = B^T * C^-1 * B^T

    MathUtil mathUtil = new MathUtil(covarianceMatrix);

    // C^(-1)
    RealMatrix xxMatrix = null;

    xxMatrix = mathUtil.invert();

//    if (!mathUtil.isZeroMatrix()) {
//      xxMatrix = mathUtil.invert();
//    } else {
//
//      // this is a special case for convolution in which the "summing" variable has no variance itself
//      // although a 0 variance is not valid in general
//      xxMatrix = covarianceMatrix;
//    }

    // B^T * C^(-1)
    RealMatrix weightedXXMatrix = weightMatrix.transpose().multiply(xxMatrix);

    // -B^T * C^(-1)
    RealMatrix yxMatrix = weightedXXMatrix.scalarMultiply(-1.0d);

    // -C^(-1)^T * B
    RealMatrix xyMatrix = xxMatrix.transpose().multiply(weightMatrix).scalarMultiply(-1.0d);

    // B^T * C^(-1) * B
    RealMatrix yyMatrix = weightedXXMatrix.multiply(weightMatrix);

    // K
    RealMatrix conditionedPrecisionMatrix = new Array2DRowRealMatrix(scope.size(), scope.size());

    // Matrix to generate h
    RealMatrix conditionedMeanTransformationMatrix = new Array2DRowRealMatrix(scope.size(), scope.size());

    Scope predictionScope = scope.reduceBy(conditioningScope);
    int[] predictionMapping = scope.createContinuousVariableMapping(predictionScope);
    int[] conditioningMapping = scope.createContinuousVariableMapping(conditioningScope);

    for (int i = 0; i < scope.size(); i++) {
      RealVector precisionColumn = conditionedPrecisionMatrix.getColumnVector(i);

      if (predictionMapping[i] >= 0) {
        precisionColumn = precisionColumn.add(padVector(xxMatrix.getColumnVector(predictionMapping[i]), scope.size(), predictionMapping));

        conditionedMeanTransformationMatrix.setColumnVector(i, precisionColumn);

        precisionColumn = precisionColumn.add(padVector(yxMatrix.getColumnVector(predictionMapping[i]), scope.size(), conditioningMapping));

        conditionedPrecisionMatrix.setColumnVector(i, precisionColumn);
      }

      if (conditioningMapping[i] >= 0) {
        precisionColumn = precisionColumn.add(padVector(xyMatrix.getColumnVector(conditioningMapping[i]), scope.size(), predictionMapping));

        conditionedMeanTransformationMatrix.setColumnVector(i, precisionColumn);

        precisionColumn = precisionColumn.add(padVector(yyMatrix.getColumnVector(conditioningMapping[i]), scope.size(), conditioningMapping));

        conditionedPrecisionMatrix.setColumnVector(i, precisionColumn);
      }
    }

    // h = (a, 0)^T * (XX, XY; 0, 0)
    RealMatrix scaledMeanMatrix = new Array2DRowRealMatrix(1, scope.size());
    scaledMeanMatrix.setRowVector(0, padVector(meanVector, scope.size(), predictionMapping));

    scaledMeanMatrix = scaledMeanMatrix.multiply(conditionedMeanTransformationMatrix);
    RealVector scaledMeanVector = scaledMeanMatrix.getRowVector(0);

    // g = a^T * C^-1 * a - log((2 * PI) ^ m/2 * det(C)^0.5) where m is the size of the prediction scope
    RealMatrix meanMatrix = new Array2DRowRealMatrix(predictionScope.size(), 1);
    meanMatrix.setColumnVector(0, meanVector);
    double normalizationConstant = - 0.5d * meanVector.dotProduct(xxMatrix.operate(meanVector))
        - Math.log(Math.pow(2 * Math.PI, (double) predictionScope.size() / 2.0d) * Math.sqrt(mathUtil.determinant()));

    return new CanonicalGaussianFactor(scope, conditionedPrecisionMatrix, scaledMeanVector, normalizationConstant);

  }

}
