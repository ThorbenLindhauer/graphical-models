package com.github.thorbenlindhauer.factor;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import com.github.thorbenlindhauer.variable.DiscreteVariable;

public class DiscreteVariableTest {

  @Test
  public void testEquals() {
    DiscreteVariable variable1 = new DiscreteVariable("A", 25);
    DiscreteVariable variable2 = new DiscreteVariable("A", 25);
    
    assertThat(variable1).isEqualTo(variable2);
    
  }
}
