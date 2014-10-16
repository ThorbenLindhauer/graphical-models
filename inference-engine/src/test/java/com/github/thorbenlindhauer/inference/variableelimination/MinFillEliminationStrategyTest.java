package com.github.thorbenlindhauer.inference.variableelimination;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.thorbenlindhauer.network.GraphicalModel;

public class MinFillEliminationStrategyTest {

  protected MinFillEliminationStrategy eliminationStrategy;
  
  @Before
  public void setUp() {
    eliminationStrategy = new MinFillEliminationStrategy();
  }
  
  @Test
  public void testThreeVariables() {
    // model: C <- A -> B
    GraphicalModel model = GraphicalModel.create()
    .variable("A", 2).variable("B", 2).variable("C", 2).done()
    .factor()
      .scope("A")
      .basedOnTable(new double[] {0.1, 0.4})
    .factor()
      .scope("A", "C")
      .basedOnTable(new double[] {0.5, 0.2, 0.3, 0.4})
    .factor()
      .scope("A", "B")
      .basedOnTable(new double[] {
        0.3, 0.6, 0.7, // values are irrelevant for this test
        0.2
      })
    .build();
    
    // A should come last since eliminating it in the first step introduces a fill edge
    List<String> eliminationOrder = eliminationStrategy.getEliminationOrder(model, Arrays.asList("A", "B"));
    assertThat(eliminationOrder).isEqualTo(Arrays.asList("B", "A"));
    
    eliminationOrder = eliminationStrategy.getEliminationOrder(model, Arrays.asList("C", "A"));
    assertThat(eliminationOrder).isEqualTo(Arrays.asList("C", "A"));
  }
  
  @Test
  public void testFiveVariables() {
    // model: D -> A -> B
    //        | \  |
    //        v  \ v
    //        E -> C
    GraphicalModel model = GraphicalModel.create()
    .variable("A", 1).variable("B", 1).variable("C", 1).variable("D", 1).variable("E", 1).done()
    .factor()
      .scope("D")
      .basedOnTable(new double[] {1})
    .factor()
      .scope("D", "A")
      .basedOnTable(new double[] {1})
    .factor()
      .scope("D", "E")
      .basedOnTable(new double[] {1})
    .factor()
      .scope("E", "C")
      .basedOnTable(new double[] {1})
    .factor()
      .scope("A", "B")
      .basedOnTable(new double[] {1})
    .factor()
      .scope("A", "C")
      .basedOnTable(new double[] {1})
    .factor()
      .scope("D", "C")
      .basedOnTable(new double[] {1})
    .build();
    
    // in this model, the order B, A, D is the only one that does not introduce any fill edges
    List<String> eliminationOrder = eliminationStrategy.getEliminationOrder(model, Arrays.asList("D", "A", "B"));
    assertThat(eliminationOrder).isEqualTo(Arrays.asList("B", "A", "D"));
    
    // eliminate E and D
    eliminationOrder = eliminationStrategy.getEliminationOrder(model, Arrays.asList("D", "E"));
    assertThat(eliminationOrder).isEqualTo(Arrays.asList("E", "D"));
    
    // eliminate E and A
    eliminationOrder = eliminationStrategy.getEliminationOrder(model, Arrays.asList("E", "A"));
    assertThat(eliminationOrder).isEqualTo(Arrays.asList("E", "A"));
    
    // eliminate C and A
    eliminationOrder = eliminationStrategy.getEliminationOrder(model, Arrays.asList("A", "C"));
    assertThat(eliminationOrder).isEqualTo(Arrays.asList("C", "A"));
    
    // eliminate A, B, C
    eliminationOrder = eliminationStrategy.getEliminationOrder(model, Arrays.asList("A", "B", "C"));
    assertThat(eliminationOrder).isEqualTo(Arrays.asList("B", "A", "C"));
    
  }
}
