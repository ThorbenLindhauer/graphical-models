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
package com.github.thorbenlindhauer.test.util;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import org.apache.commons.math3.linear.MatrixUtils;

/**
 * @author Thorben
 *
 */
public class LinearAlgebraUtil {

  public static RealVector asVector(double value) {
    return new ArrayRealVector(new double[]{ value });
  }

  public static RealMatrix asMatrix(double... values) {
    return new Array2DRowRealMatrix(new double[][]{ values });
  }

  public static RealMatrix asSingleRowMatrix(double value, int repetitions) {
    double[] values = new double[repetitions];
    for (int i = 0; i < repetitions; i++) {
      values[i] = value;
    }

    RealMatrix matrix = new Array2DRowRealMatrix(1, repetitions);
    matrix.setRow(0, values);
    return matrix;
  }

  public static RealMatrix asRowMatrix(double... values) {
    RealMatrix matrix = new Array2DRowRealMatrix(1, values.length);
    matrix.setRow(0, values);
    return matrix;
  }

  public static RealMatrix diagonalMatrix(double value, int size) {
    double[] values = new double[size];
    for (int i = 0; i < size; i++) {
      values[i] = value;
    }

    return MatrixUtils.createRealDiagonalMatrix(values);
  }
}
