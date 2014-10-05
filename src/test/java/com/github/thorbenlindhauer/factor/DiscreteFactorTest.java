package com.github.thorbenlindhauer.factor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.github.thorbenlindhauer.variable.DiscreteVariable;
import com.github.thorbenlindhauer.variable.Variables;

public class DiscreteFactorTest {

  
  @Test
  public void testFactorProduct() {
    Variables variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 5));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1, 
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6, // B == 2
          7, 8, // B == 3
          9, 10 // B == 4
       });
    
    Variables variablesFactor2 = newVariables(new DiscreteVariable("B", 5), new DiscreteVariable("C", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2, 
        new double[] {
          1, 2, 3, 4, 5,        // C == 0
          6, 7, 8, 9, 10,       // C == 1
          11, 12, 13, 14, 15    // C == 2
        });
    
    TableBasedDiscreteFactor product = factor1.product(factor2);
    Collection<DiscreteVariable> newVariables = product.getVariables().getVariables();
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
    Variables variablesFactor1 = newVariables(new DiscreteVariable("A", 2), new DiscreteVariable("B", 3));
    TableBasedDiscreteFactor factor1 = new TableBasedDiscreteFactor(variablesFactor1, 
        new double[] {
          1, 2, // B == 0
          3, 4, // B == 1
          5, 6, // B == 2
       });
    
    Variables variablesFactor2 = newVariables(new DiscreteVariable("C", 3));
    TableBasedDiscreteFactor factor2 = new TableBasedDiscreteFactor(variablesFactor2, 
        new double[] {
          1, 2, 3
        });
    
    TableBasedDiscreteFactor product = factor1.product(factor2);
    Collection<DiscreteVariable> newVariables = product.getVariables().getVariables();
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
  
  protected Variables newVariables(DiscreteVariable... variables) {
    Set<DiscreteVariable> variableArgs = new HashSet<DiscreteVariable>();
    for (DiscreteVariable variable : variables) {
      variableArgs.add(variable);
    }
    
    return new Variables(variableArgs);
  }
}
