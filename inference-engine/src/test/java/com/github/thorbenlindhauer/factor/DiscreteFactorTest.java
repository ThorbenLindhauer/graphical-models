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

import org.junit.Test;

import com.github.thorbenlindhauer.exception.FactorOperationException;
import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

public class DiscreteFactorTest {


  @Test
  public void testFactorProduct() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 5));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6, // B == 2
          7, 8, // B == 3
          9, 10 // B == 4
       });

    Scope variablesFactor2 = newVariables(new DiscreteVariable("B", 5), new DiscreteVariable("C", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          1, 2, 3, 4, 5,        // C == 0
          6, 7, 8, 9, 10,       // C == 1
          11, 12, 13, 14, 15    // C == 2
        });

    TableBasedDiscreteFactor product = factor1.product(factor2);
    Collection<Variable> newVariables = product.getVariables().getVariables();
    assertThat(newVariables).hasSize(3);
    assertThat(newVariables).containsAll(variablesFactor1.getVariables());
    assertThat(newVariables).containsAll(variablesFactor2.getVariables());

    double[] newValues = product.getValues();
    double[] factor1Values = factor1.getValues();
    double[] factor2Values = factor2.getValues();

    assertThat(newValues[0]).isEqualTo(factor1Values[0] * factor2Values[0]); // a = 0, b = 0, c = 0
    assertThat(newValues[1]).isEqualTo(factor1Values[1] * factor2Values[0]); // a = 1, b = 0, c = 0
    assertThat(newValues[2]).isEqualTo(factor1Values[2] * factor2Values[1]); // a = 0, b = 1, c = 0
    assertThat(newValues[3]).isEqualTo(factor1Values[3] * factor2Values[1]);
    assertThat(newValues[4]).isEqualTo(factor1Values[4] * factor2Values[2]);
    assertThat(newValues[5]).isEqualTo(factor1Values[5] * factor2Values[2]);
    assertThat(newValues[6]).isEqualTo(factor1Values[6] * factor2Values[3]);
    assertThat(newValues[7]).isEqualTo(factor1Values[7] * factor2Values[3]);
    assertThat(newValues[8]).isEqualTo(factor1Values[8] * factor2Values[4]);
    assertThat(newValues[9]).isEqualTo(factor1Values[9] * factor2Values[4]);
    assertThat(newValues[10]).isEqualTo(factor1Values[0] * factor2Values[5]);
    assertThat(newValues[11]).isEqualTo(factor1Values[1] * factor2Values[5]);
    assertThat(newValues[12]).isEqualTo(factor1Values[2] * factor2Values[6]);
    assertThat(newValues[13]).isEqualTo(factor1Values[3] * factor2Values[6]);
    assertThat(newValues[14]).isEqualTo(factor1Values[4] * factor2Values[7]);
    assertThat(newValues[15]).isEqualTo(factor1Values[5] * factor2Values[7]);
    assertThat(newValues[16]).isEqualTo(factor1Values[6] * factor2Values[8]);
    assertThat(newValues[17]).isEqualTo(factor1Values[7] * factor2Values[8]);
    assertThat(newValues[18]).isEqualTo(factor1Values[8] * factor2Values[9]);
    assertThat(newValues[19]).isEqualTo(factor1Values[9] * factor2Values[9]);
    assertThat(newValues[20]).isEqualTo(factor1Values[0] * factor2Values[10]);
    assertThat(newValues[21]).isEqualTo(factor1Values[1] * factor2Values[10]);
    assertThat(newValues[22]).isEqualTo(factor1Values[2] * factor2Values[11]);
    assertThat(newValues[23]).isEqualTo(factor1Values[3] * factor2Values[11]);
    assertThat(newValues[24]).isEqualTo(factor1Values[4] * factor2Values[12]);
    assertThat(newValues[25]).isEqualTo(factor1Values[5] * factor2Values[12]);
    assertThat(newValues[26]).isEqualTo(factor1Values[6] * factor2Values[13]);
    assertThat(newValues[27]).isEqualTo(factor1Values[7] * factor2Values[13]);
    assertThat(newValues[28]).isEqualTo(factor1Values[8] * factor2Values[14]);
    assertThat(newValues[29]).isEqualTo(factor1Values[9] * factor2Values[14]);
  }

  @Test
  public void testFactorProductOfIndependentFactors() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6, // B == 2
       });

    Scope variablesFactor2 = newVariables(new DiscreteVariable("C", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          1, 2, 3
        });

    TableBasedDiscreteFactor product = factor1.product(factor2);
    Collection<Variable> newVariables = product.getVariables().getVariables();
    assertThat(newVariables).hasSize(3);
    assertThat(newVariables).containsAll(variablesFactor1.getVariables());
    assertThat(newVariables).containsAll(variablesFactor2.getVariables());

    double[] newValues = product.getValues();
    double[] factor1Values = factor1.getValues();
    double[] factor2Values = factor2.getValues();

    assertThat(newValues[0]).isEqualTo(factor1Values[0] * factor2Values[0]); // a = 0, b = 0, c = 0
    assertThat(newValues[1]).isEqualTo(factor1Values[1] * factor2Values[0]); // a = 1, b = 0, c = 0
    assertThat(newValues[2]).isEqualTo(factor1Values[2] * factor2Values[0]); // a = 0, b = 1, c = 0
    assertThat(newValues[3]).isEqualTo(factor1Values[3] * factor2Values[0]);
    assertThat(newValues[4]).isEqualTo(factor1Values[4] * factor2Values[0]);
    assertThat(newValues[5]).isEqualTo(factor1Values[5] * factor2Values[0]);
    assertThat(newValues[6]).isEqualTo(factor1Values[0] * factor2Values[1]);
    assertThat(newValues[7]).isEqualTo(factor1Values[1] * factor2Values[1]);
    assertThat(newValues[8]).isEqualTo(factor1Values[2] * factor2Values[1]);
    assertThat(newValues[9]).isEqualTo(factor1Values[3] * factor2Values[1]);
    assertThat(newValues[10]).isEqualTo(factor1Values[4] * factor2Values[1]);
    assertThat(newValues[11]).isEqualTo(factor1Values[5] * factor2Values[1]);
    assertThat(newValues[12]).isEqualTo(factor1Values[0] * factor2Values[2]);
    assertThat(newValues[13]).isEqualTo(factor1Values[1] * factor2Values[2]);
    assertThat(newValues[14]).isEqualTo(factor1Values[2] * factor2Values[2]);
    assertThat(newValues[15]).isEqualTo(factor1Values[3] * factor2Values[2]);
    assertThat(newValues[16]).isEqualTo(factor1Values[4] * factor2Values[2]);
    assertThat(newValues[17]).isEqualTo(factor1Values[5] * factor2Values[2]);
  }

  @Test
  public void testFactorProductWithConstantValueFactor() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6, // B == 2
       });

    Scope variablesFactor2 = newVariables();
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          2
        });

    TableBasedDiscreteFactor product = factor1.product(factor2);
    assertThat(product.getValues()).hasSize(6);
    assertThat(product.getValues()[0]).isEqualTo(2);
    assertThat(product.getValues()[1]).isEqualTo(4);
    assertThat(product.getValues()[2]).isEqualTo(6);
    assertThat(product.getValues()[3]).isEqualTo(8);
    assertThat(product.getValues()[4]).isEqualTo(10);
    assertThat(product.getValues()[5]).isEqualTo(12);

    product = factor2.product(factor1);
    assertThat(product.getValues()).hasSize(6);
    assertThat(product.getValues()[0]).isEqualTo(2);
    assertThat(product.getValues()[1]).isEqualTo(4);
    assertThat(product.getValues()[2]).isEqualTo(6);
    assertThat(product.getValues()[3]).isEqualTo(8);
    assertThat(product.getValues()[4]).isEqualTo(10);
    assertThat(product.getValues()[5]).isEqualTo(12);

  }

  @Test
  public void testFactorMarginalTwoVariables() {
    Scope variables = newVariables(new DiscreteVariable("A", 3), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(variables,
        new double[] {
          1, 2, 3, // B == 0
          4, 5, 6, // B == 1
          7, 8, 9  // B == 2
       });

    Scope variablesOfMarginal = newVariables(new DiscreteVariable("A", 3));
    TableBasedDiscreteFactor marginalFactor = factor.marginal(variablesOfMarginal);
    assertThat(marginalFactor.getVariables().getVariables()).hasSize(1);
    assertThat(marginalFactor.getVariables().getVariables()).contains(new DiscreteVariable("A", 3));

    assertThat(marginalFactor.getValues()).hasSize(3);
    assertThat(marginalFactor.getValues()[0]).isEqualTo(12);
    assertThat(marginalFactor.getValues()[1]).isEqualTo(15);
    assertThat(marginalFactor.getValues()[2]).isEqualTo(18);
  }

  @Test
  public void testFactorMarginalThreeVariablesCase1() {
    Scope variables = newVariables(new DiscreteVariable("A", 3), new DiscreteVariable("B", 3), new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(variables,
        new double[] {
          1, 2, 3, // B == 0, C == 0
          4, 5, 6, // B == 1, C == 0
          7, 8, 9, // B == 2, C == 0
          10, 11, 12, // B == 0, C == 1
          13, 14, 15, // B == 1, C == 1
          16, 17, 18  // B == 2, C == 1
       });

    Scope variablesOfMarginalA = newVariables(new DiscreteVariable("A", 3));
    TableBasedDiscreteFactor marginalFactorA = factor.marginal(variablesOfMarginalA);
    assertThat(marginalFactorA.getVariables().getVariables()).hasSize(1);
    assertThat(marginalFactorA.getVariables().getVariables()).contains(new DiscreteVariable("A", 3));

    assertThat(marginalFactorA.getValues()).hasSize(3);
    assertThat(marginalFactorA.getValues()[0]).isEqualTo(51);
    assertThat(marginalFactorA.getValues()[1]).isEqualTo(57);
    assertThat(marginalFactorA.getValues()[2]).isEqualTo(63);
  }

  @Test
  public void testFactorMarginalThreeVariablesCase2() {
    Scope variables = newVariables(new DiscreteVariable("A", 3), new DiscreteVariable("B", 3), new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(variables,
        new double[] {
          1, 2, 3, // B == 0, C == 0
          4, 5, 6, // B == 1, C == 0
          7, 8, 9, // B == 2, C == 0
          10, 11, 12, // B == 0, C == 1
          13, 14, 15, // B == 1, C == 1
          16, 17, 18  // B == 2, C == 1
       });

    Scope variablesOfMarginalB = newVariables(new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor marginalFactorB = factor.marginal(variablesOfMarginalB);
    assertThat(marginalFactorB.getVariables().getVariables()).hasSize(1);
    assertThat(marginalFactorB.getVariables().getVariables()).contains(new DiscreteVariable("B", 3));

    assertThat(marginalFactorB.getValues()).hasSize(3);
    assertThat(marginalFactorB.getValues()[0]).isEqualTo(39);
    assertThat(marginalFactorB.getValues()[1]).isEqualTo(57);
    assertThat(marginalFactorB.getValues()[2]).isEqualTo(75);

  }

  @Test
  public void testFactorMarginalThreeVariablesCase3() {
    Scope variables = newVariables(new DiscreteVariable("A", 3), new DiscreteVariable("B", 3), new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(variables,
        new double[] {
          1, 2, 3, // B == 0, C == 0
          4, 5, 6, // B == 1, C == 0
          7, 8, 9, // B == 2, C == 0
          10, 11, 12, // B == 0, C == 1
          13, 14, 15, // B == 1, C == 1
          16, 17, 18  // B == 2, C == 1
       });


    Scope variablesOfMarginalC = newVariables(new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor marginalFactorC = factor.marginal(variablesOfMarginalC);
    assertThat(marginalFactorC.getVariables().getVariables()).hasSize(1);
    assertThat(marginalFactorC.getVariables().getVariables()).contains(new DiscreteVariable("C", 2));

    assertThat(marginalFactorC.getValues()).hasSize(2);
    assertThat(marginalFactorC.getValues()[0]).isEqualTo(45);
    assertThat(marginalFactorC.getValues()[1]).isEqualTo(126);

  }

  @Test
  public void testFactorMarginalThreeVariablesCase4() {
    Scope variables = newVariables(new DiscreteVariable("A", 3), new DiscreteVariable("B", 3), new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(variables,
        new double[] {
          1, 2, 3, // B == 0, C == 0
          4, 5, 6, // B == 1, C == 0
          7, 8, 9, // B == 2, C == 0
          10, 11, 12, // B == 0, C == 1
          13, 14, 15, // B == 1, C == 1
          16, 17, 18  // B == 2, C == 1
       });

    Scope variablesOfMarginalBC = newVariables(new DiscreteVariable("B", 3), new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor marginalFactorBC = factor.marginal(variablesOfMarginalBC);
    assertThat(marginalFactorBC.getVariables().getVariables()).hasSize(2);
    assertThat(marginalFactorBC.getVariables().getVariables()).contains(new DiscreteVariable("B", 3));
    assertThat(marginalFactorBC.getVariables().getVariables()).contains(new DiscreteVariable("C", 2));

    assertThat(marginalFactorBC.getValues()).hasSize(6);
    assertThat(marginalFactorBC.getValues()[0]).isEqualTo(6); // B == 0, C == 0
    assertThat(marginalFactorBC.getValues()[1]).isEqualTo(15); // B == 1, C == 0
    assertThat(marginalFactorBC.getValues()[2]).isEqualTo(24); // B == 2, C == 0
    assertThat(marginalFactorBC.getValues()[3]).isEqualTo(33); // B == 0, C == 1
    assertThat(marginalFactorBC.getValues()[4]).isEqualTo(42); // B == 1, C == 1
    assertThat(marginalFactorBC.getValues()[5]).isEqualTo(51); // B == 2, C == 1
  }

  @Test
  public void testValueObservation() {
    Scope scope = newVariables(new DiscreteVariable("A", 3), new DiscreteVariable("B", 3), new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(scope,
        new double[] {
          1, 2, 3, // B == 0, C == 0
          4, 5, 6, // B == 1, C == 0
          7, 8, 9, // B == 2, C == 0
          10, 11, 12, // B == 0, C == 1
          13, 14, 15, // B == 1, C == 1
          16, 17, 18  // B == 2, C == 1
       });

    TableBasedDiscreteFactor observedValuesFactor = factor.observation(scope.subScope("A"), new int[] {0});

    double[] valuesAfterObservation = observedValuesFactor.getValues();
    assertThat(valuesAfterObservation).isEqualTo(new double[] {
        1, 0, 0, // B == 0, C == 0
        4, 0, 0, // B == 1, C == 0
        7, 0, 0, // B == 2, C == 0
        10, 0, 0, // B == 0, C == 1
        13, 0, 0, // B == 1, C == 1
        16, 0, 0  // B == 2, C == 1
     });
  }

  @Test
  public void testValueObservationCase2() {
    Scope scope = newVariables(new DiscreteVariable("A", 3), new DiscreteVariable("B", 3), new DiscreteVariable("C", 2));
    TableBasedDiscreteFactor factor = new TableBasedDiscreteFactor(scope,
        new double[] {
          1, 2, 3, // B == 0, C == 0
          4, 5, 6, // B == 1, C == 0
          7, 8, 9, // B == 2, C == 0
          10, 11, 12, // B == 0, C == 1
          13, 14, 15, // B == 1, C == 1
          16, 17, 18  // B == 2, C == 1
       });

    TableBasedDiscreteFactor observedValuesFactor = factor.observation(scope.subScope("A", "C"), new int[] {0, 1});

    double[] valuesAfterObservation = observedValuesFactor.getValues();
    assertThat(valuesAfterObservation).isEqualTo(new double[] {
        0, 0, 0, // B == 0, C == 0
        0, 0, 0, // B == 1, C == 0
        0, 0, 0, // B == 2, C == 0
        10, 0, 0, // B == 0, C == 1
        13, 0, 0, // B == 1, C == 1
        16, 0, 0  // B == 2, C == 1
     });
  }

  @Test
  public void testFactorDivision() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6  // B == 2
       });

    Scope variablesFactor2 = newVariables(new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          1, 2, 3
        });

    TableBasedDiscreteFactor quotient = factor1.division(factor2);
    Collection<Variable> newVariables = quotient.getVariables().getVariables();
    assertThat(newVariables).hasSize(2);
    assertThat(newVariables).containsAll(variablesFactor1.getVariables());

    double[] newValues = quotient.getValues();
    double[] factor1Values = factor1.getValues();
    double[] factor2Values = factor2.getValues();

    assertThat(newValues[0]).isEqualTo(factor1Values[0] / factor2Values[0]); // a = 0, b = 0
    assertThat(newValues[1]).isEqualTo(factor1Values[1] / factor2Values[0]); // a = 1, b = 0
    assertThat(newValues[2]).isEqualTo(factor1Values[2] / factor2Values[1]); // a = 0, b = 1
    assertThat(newValues[3]).isEqualTo(factor1Values[3] / factor2Values[1]);
    assertThat(newValues[4]).isEqualTo(factor1Values[4] / factor2Values[2]);
    assertThat(newValues[5]).isEqualTo(factor1Values[5] / factor2Values[2]);
  }

  @Test
  public void testFactorDivisionCase2() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3), new DiscreteVariable("C", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          1, 2,   // B == 0, C == 0
          3, 4,   // B == 1, C == 0
          5, 6,   // B == 2, C == 0
          7, 8,   // B == 0, C == 1
          9, 10,  // B == 1, C == 1
          11, 12, // B == 2, C == 1
          13, 14, // B == 0, C == 2
          15, 16, // B == 1, C == 2
          17, 18  // B == 2, C == 2
       });

    Scope variablesFactor2 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("C", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          1, 2, // C == 0
          3, 4, // C == 1
          5, 6  // C == 2
        });

    TableBasedDiscreteFactor quotient = factor1.division(factor2);
    Collection<Variable> newVariables = quotient.getVariables().getVariables();
    assertThat(newVariables).hasSize(3);
    assertThat(newVariables).containsAll(variablesFactor1.getVariables());

    double[] newValues = quotient.getValues();
    double[] factor1Values = factor1.getValues();
    double[] factor2Values = factor2.getValues();

    assertThat(newValues[0]).isEqualTo(factor1Values[0] / factor2Values[0]); // a = 0, b = 0, c = 0
    assertThat(newValues[1]).isEqualTo(factor1Values[1] / factor2Values[1]); // a = 1, b = 0, c = 0
    assertThat(newValues[2]).isEqualTo(factor1Values[2] / factor2Values[0]); // a = 0, b = 1, c = 0
    assertThat(newValues[3]).isEqualTo(factor1Values[3] / factor2Values[1]);
    assertThat(newValues[4]).isEqualTo(factor1Values[4] / factor2Values[0]);
    assertThat(newValues[5]).isEqualTo(factor1Values[5] / factor2Values[1]);
    assertThat(newValues[6]).isEqualTo(factor1Values[6] / factor2Values[2]);
    assertThat(newValues[7]).isEqualTo(factor1Values[7] / factor2Values[3]);
    assertThat(newValues[8]).isEqualTo(factor1Values[8] / factor2Values[2]);
    assertThat(newValues[9]).isEqualTo(factor1Values[9] / factor2Values[3]);
    assertThat(newValues[10]).isEqualTo(factor1Values[10] / factor2Values[2]);
    assertThat(newValues[11]).isEqualTo(factor1Values[11] / factor2Values[3]);
    assertThat(newValues[12]).isEqualTo(factor1Values[12] / factor2Values[4]);
    assertThat(newValues[13]).isEqualTo(factor1Values[13] / factor2Values[5]);
    assertThat(newValues[14]).isEqualTo(factor1Values[14] / factor2Values[4]);
    assertThat(newValues[15]).isEqualTo(factor1Values[15] / factor2Values[5]);
    assertThat(newValues[16]).isEqualTo(factor1Values[16] / factor2Values[4]);
    assertThat(newValues[17]).isEqualTo(factor1Values[17] / factor2Values[5]);
  }

  @Test
  public void testFactorDivisionMismatchingScopes() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6 // B == 2
       });

    Scope variablesFactor2 = newVariables(new DiscreteVariable("C", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          1, 2, 3
        });

    try {
      factor1.division(factor2);
      fail("expected exception");
    } catch (FactorOperationException e) {
      // happy path
    }
  }

  @Test
  public void testFactorDivisionDivideZeroByZero() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          0, 0, // B == 0
          0, 4, // B == 1
          0, 6 // B == 2
       });

    Scope variablesFactor2 = newVariables(new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          0, 2, 3
        });

    TableBasedDiscreteFactor quotient = factor1.division(factor2);
    Collection<Variable> newVariables = quotient.getVariables().getVariables();
    assertThat(newVariables).hasSize(2);
    assertThat(newVariables).containsAll(variablesFactor1.getVariables());

    double[] newValues = quotient.getValues();
    double[] factor1Values = factor1.getValues();
    double[] factor2Values = factor2.getValues();

    assertThat(newValues[0]).isEqualTo(0);                                    // a = 0, b = 0
    assertThat(newValues[1]).isEqualTo(0);                                    // a = 1, b = 0
    assertThat(newValues[2]).isEqualTo(factor1Values[2] / factor2Values[1]);  // a = 0, b = 1
    assertThat(newValues[3]).isEqualTo(factor1Values[3] / factor2Values[1]);
    assertThat(newValues[4]).isEqualTo(factor1Values[4] / factor2Values[2]);
    assertThat(newValues[5]).isEqualTo(factor1Values[5] / factor2Values[2]);
  }

  @Test
  public void testFactorDivisionDivideNonZeroByZero() {
    Scope variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1,
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6 // B == 2
       });

    Scope variablesFactor2 = newVariables(new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2,
        new double[] {
          0, 2, 3
        });

    try {
      factor1.division(factor2);
      fail("expected exception");
    } catch (FactorOperationException e) {
      // happy path
    }
  }

  protected Scope newVariables(DiscreteVariable... variables) {
    Set<DiscreteVariable> variableArgs = new HashSet<DiscreteVariable>();
    for (DiscreteVariable variable : variables) {
      variableArgs.add(variable);
    }

    return new Scope(variableArgs);
  }
}
