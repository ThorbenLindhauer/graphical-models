package com.github.thorbenlindhauer.inference;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import com.github.thorbenlindhauer.network.GraphicalModel;

public class NaiveInferencerTest {
  
  protected static final Offset<Double> DOUBLE_VALUE_TOLERANCE = Offset.offset(0.00001d);

  @Before
  public void setUp() {
    
  }
  
  /**
   * Creates a model like A -> C <- B, where there is a CPD P(C | A, B)
   * and two priors P(A) and P(B).
   */
  @Test
  public void testSimpleNaiveInference() {
    GraphicalModel model = GraphicalModel.create()
      .variable("A", 3).variable("B", 3).variable("C", 2).done()
      .factor()
        .scope("A")
        .basedOnTable(new double[] {0.1, 0.4, 0.5})
      .factor()
        .scope("B")
        .basedOnTable(new double[] {0.5, 0.2, 0.3})
      .factor()
        .scope("A", "B", "C")
        .basedOnTable(new double[] {
          0.3, 0.6, 0.7,  // B == 0, C == 0
          0.2, 0.6, 0.8,  // B == 1, C == 0
          0.25, 0.6, 0.7, // B == 2, C == 0
          0.7, 0.4, 0.3,  // B == 0, C == 1
          0.8, 0.4, 0.2,  // B == 1, C == 1
          0.75, 0.4, 0.3  // B == 2, C == 1
        })
      .build();
      
    ExactInferencer inferencer = new NaiveInferencer(model);
    double jointZeroProbability = inferencer.jointProbability(new int[] {0}, model.getScope().subScope("C"));
    assertThat(jointZeroProbability).isEqualTo(0.6265d, DOUBLE_VALUE_TOLERANCE);
    
    double jointOneProbability = inferencer.jointProbability(new int[] {1}, model.getScope().subScope("C"));
    assertThat(jointOneProbability).isEqualTo(0.3735d, DOUBLE_VALUE_TOLERANCE);
  }
}
