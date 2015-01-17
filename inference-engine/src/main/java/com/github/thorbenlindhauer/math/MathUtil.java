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
package com.github.thorbenlindhauer.math;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.github.thorbenlindhauer.exception.FactorOperationException;

/**
 * @author Thorben
 *
 */
public class MathUtil {

  protected RealMatrix matrix;
  protected LUDecomposition luDecomposition;

  public MathUtil(RealMatrix matrix) {
    this.matrix = matrix;
  }

  protected void ensureLUDecompositionInitialized() {
    if (luDecomposition == null) {
      luDecomposition = new LUDecomposition(matrix);
    }
  }

  public RealMatrix invert() {
    if (!matrix.isSquare()) {
      throw new FactorOperationException("Cannot invert non-square matrix");
    }
    ensureLUDecompositionInitialized();

    int matrixDimension = matrix.getRowDimension();

    RealMatrix inverseMatrix = new Array2DRowRealMatrix(matrixDimension, matrixDimension);
    RealMatrix identityMatrix = MatrixUtils.createRealIdentityMatrix(matrixDimension);

    DecompositionSolver solver = luDecomposition.getSolver();

    for (int i = 0; i < matrixDimension; i++) {
      RealVector identityColumn = identityMatrix.getColumnVector(i);
      RealVector inverseColumn = solver.solve(identityColumn);

      inverseMatrix.setColumnVector(i, inverseColumn);
    }

    return inverseMatrix;

  }

  public double determinant() {
    ensureLUDecompositionInitialized();

    return luDecomposition.getDeterminant();
  }

  protected double DOUBLE_COMPARISON_OFFSET = 10e-10;

  public boolean isZeroMatrix() {

    final AtomicBoolean isZeroMatrix = new AtomicBoolean(true);

    // TODO: optimize to stop after first non-zero entry
    matrix.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {

      @Override
      public void visit(int row, int column, double value) {
        if (value > DOUBLE_COMPARISON_OFFSET || value < - DOUBLE_COMPARISON_OFFSET) {
          isZeroMatrix.set(false);
        }
      }
    });

    return isZeroMatrix.get();
  }
}
