package com.github.thorbenlindhauer.factor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.thorbenlindhauer.variable.IndexCoder;
import com.github.thorbenlindhauer.variable.IndexMapper;

public class IndexMapperTest {

  @Test
  public void testMapping() {
    int[] variableCardinalities = new int[] {3, 5, 2};
    IndexCoder coder = new IndexCoder(variableCardinalities);
    
    int[] mapping = new int[] {2, 0, 1}; // i.e. mapped cardinalities: 5, 2, 3
    IndexMapper mapper = coder.getIndexMapper(mapping);
    
    assertThat(mapper.mapIndex(0)).isEqualTo(0); // assignment 0 0 0 maps to 0 0 0
    assertThat(mapper.reverseMapIndex(0)).isEqualTo(0);
    
    assertThat(mapper.mapIndex(2)).isEqualTo(20); // assignment 2 0 0 maps to 0 0 2
    assertThat(mapper.reverseMapIndex(20)).isEqualTo(2);
    
    assertThat(mapper.mapIndex(23)).isEqualTo(27); // assignment 2 2 1 maps to 2 1 2
    assertThat(mapper.reverseMapIndex(27)).isEqualTo(23);
  }
}
