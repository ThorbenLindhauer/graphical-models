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

import org.junit.Test;

import com.github.thorbenlindhauer.variable.IndexCoder;

public class IndexCoderTest {

  @Test
  public void testAssignmentToIndex() {
    int[] variableCardinalities = new int[] {3, 5, 2};
    IndexCoder coder = new IndexCoder(variableCardinalities);
    
    assertThat(coder.getIndexForAssignment(new int[] {0, 0, 0})).isEqualTo(0);
    assertThat(coder.getIndexForAssignment(new int[] {1, 0, 0})).isEqualTo(1);
    assertThat(coder.getIndexForAssignment(new int[] {2, 0, 0})).isEqualTo(2);
    assertThat(coder.getIndexForAssignment(new int[] {0, 1, 0})).isEqualTo(3);
    assertThat(coder.getIndexForAssignment(new int[] {1, 1, 0})).isEqualTo(4);
    assertThat(coder.getIndexForAssignment(new int[] {2, 1, 0})).isEqualTo(5);
    assertThat(coder.getIndexForAssignment(new int[] {0, 2, 0})).isEqualTo(6);
    assertThat(coder.getIndexForAssignment(new int[] {1, 2, 0})).isEqualTo(7);
    assertThat(coder.getIndexForAssignment(new int[] {2, 2, 0})).isEqualTo(8);
    assertThat(coder.getIndexForAssignment(new int[] {0, 3, 0})).isEqualTo(9);
    assertThat(coder.getIndexForAssignment(new int[] {1, 3, 0})).isEqualTo(10);
    assertThat(coder.getIndexForAssignment(new int[] {2, 3, 0})).isEqualTo(11);
    assertThat(coder.getIndexForAssignment(new int[] {0, 4, 0})).isEqualTo(12);
    assertThat(coder.getIndexForAssignment(new int[] {1, 4, 0})).isEqualTo(13);
    assertThat(coder.getIndexForAssignment(new int[] {2, 4, 0})).isEqualTo(14);
    assertThat(coder.getIndexForAssignment(new int[] {0, 0, 1})).isEqualTo(15);
    assertThat(coder.getIndexForAssignment(new int[] {1, 0, 1})).isEqualTo(16);
    assertThat(coder.getIndexForAssignment(new int[] {2, 0, 1})).isEqualTo(17);
    assertThat(coder.getIndexForAssignment(new int[] {0, 1, 1})).isEqualTo(18);
    assertThat(coder.getIndexForAssignment(new int[] {1, 1, 1})).isEqualTo(19);
    assertThat(coder.getIndexForAssignment(new int[] {2, 1, 1})).isEqualTo(20);
    assertThat(coder.getIndexForAssignment(new int[] {0, 2, 1})).isEqualTo(21);
    assertThat(coder.getIndexForAssignment(new int[] {1, 2, 1})).isEqualTo(22);
    assertThat(coder.getIndexForAssignment(new int[] {2, 2, 1})).isEqualTo(23);
    assertThat(coder.getIndexForAssignment(new int[] {0, 3, 1})).isEqualTo(24);
    assertThat(coder.getIndexForAssignment(new int[] {1, 3, 1})).isEqualTo(25);
    assertThat(coder.getIndexForAssignment(new int[] {2, 3, 1})).isEqualTo(26);
    assertThat(coder.getIndexForAssignment(new int[] {0, 4, 1})).isEqualTo(27);
    assertThat(coder.getIndexForAssignment(new int[] {1, 4, 1})).isEqualTo(28);
    assertThat(coder.getIndexForAssignment(new int[] {2, 4, 1})).isEqualTo(29);
  }
  
  @Test
  public void testIndexToAssignment() {
    int[] variableCardinalities = new int[] {3, 5, 2};
    IndexCoder coder = new IndexCoder(variableCardinalities);
    
    assertThat(coder.getAssignmentForIndex(0)).isEqualTo(new int[] {0, 0, 0});
    assertThat(coder.getAssignmentForIndex(1)).isEqualTo(new int[] {1, 0, 0});
    assertThat(coder.getAssignmentForIndex(2)).isEqualTo(new int[] {2, 0, 0});
    assertThat(coder.getAssignmentForIndex(3)).isEqualTo(new int[] {0, 1, 0});
    assertThat(coder.getAssignmentForIndex(4)).isEqualTo(new int[] {1, 1, 0});
    assertThat(coder.getAssignmentForIndex(5)).isEqualTo(new int[] {2, 1, 0});
    assertThat(coder.getAssignmentForIndex(6)).isEqualTo(new int[] {0, 2, 0});
    assertThat(coder.getAssignmentForIndex(7)).isEqualTo(new int[] {1, 2, 0});
    assertThat(coder.getAssignmentForIndex(8)).isEqualTo(new int[] {2, 2, 0});
    assertThat(coder.getAssignmentForIndex(9)).isEqualTo(new int[] {0, 3, 0});
    assertThat(coder.getAssignmentForIndex(10)).isEqualTo(new int[] {1, 3, 0});
    assertThat(coder.getAssignmentForIndex(11)).isEqualTo(new int[] {2, 3, 0});
    assertThat(coder.getAssignmentForIndex(12)).isEqualTo(new int[] {0, 4, 0});
    assertThat(coder.getAssignmentForIndex(13)).isEqualTo(new int[] {1, 4, 0});
    assertThat(coder.getAssignmentForIndex(14)).isEqualTo(new int[] {2, 4, 0});
    assertThat(coder.getAssignmentForIndex(15)).isEqualTo(new int[] {0, 0, 1});
    assertThat(coder.getAssignmentForIndex(16)).isEqualTo(new int[] {1, 0, 1});
    assertThat(coder.getAssignmentForIndex(17)).isEqualTo(new int[] {2, 0, 1});
    assertThat(coder.getAssignmentForIndex(18)).isEqualTo(new int[] {0, 1, 1});
    assertThat(coder.getAssignmentForIndex(19)).isEqualTo(new int[] {1, 1, 1});
    assertThat(coder.getAssignmentForIndex(20)).isEqualTo(new int[] {2, 1, 1});
    assertThat(coder.getAssignmentForIndex(21)).isEqualTo(new int[] {0, 2, 1});
    assertThat(coder.getAssignmentForIndex(22)).isEqualTo(new int[] {1, 2, 1});
    assertThat(coder.getAssignmentForIndex(23)).isEqualTo(new int[] {2, 2, 1});
    assertThat(coder.getAssignmentForIndex(24)).isEqualTo(new int[] {0, 3, 1});
    assertThat(coder.getAssignmentForIndex(25)).isEqualTo(new int[] {1, 3, 1});
    assertThat(coder.getAssignmentForIndex(26)).isEqualTo(new int[] {2, 3, 1});
    assertThat(coder.getAssignmentForIndex(27)).isEqualTo(new int[] {0, 4, 1});
    assertThat(coder.getAssignmentForIndex(28)).isEqualTo(new int[] {1, 4, 1});
    assertThat(coder.getAssignmentForIndex(29)).isEqualTo(new int[] {2, 4, 1});
  }
  
  // TODO: test sanity of input?
}
