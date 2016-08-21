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
package com.github.thorbenlindhauer.variable;

/**
 * Maps indexes of variable assignments according to a provided mapping. Useful when input is not specified in the
 * canonical ordering.
 *
 * @author Thorben
 */
public class IndexMapper {

  protected IndexCoder originalCoder;
  protected IndexCoder mappedCoder;
  protected int[] mapping;

  public IndexMapper(IndexCoder originalCoder, IndexCoder mappedCoder, int[] mapping) {
    this.originalCoder = originalCoder;
    this.mappedCoder = mappedCoder;
    this.mapping = mapping;
  }

  public int mapIndex(int originalIndex) {
    int[] originalAssignment = originalCoder.getAssignmentForIndex(originalIndex);
    int[] mappedAssignment = map(originalAssignment);
    return mappedCoder.getIndexForAssignment(mappedAssignment);
  }

  public int reverseMapIndex(int mappedIndex) {
    int[] mappedAssignment = mappedCoder.getAssignmentForIndex(mappedIndex);
    int[] originalAssignment = reverseMap(mappedAssignment);
    return originalCoder.getIndexForAssignment(originalAssignment);
  }

  public int[] map(int[] assignment) {
    int[] mappedAssignment = new int[assignment.length];

    for (int i = 0; i < mapping.length; i++) {
      mappedAssignment[mapping[i]] = assignment[i];
    }

    return mappedAssignment;
  }

  public int[] reverseMap(int[] assignment) {
    int[] mappedAssignment = new int[assignment.length];

    for (int i = 0; i < mapping.length; i++) {
      mappedAssignment[i] = assignment[mapping[i]];
    }

    return mappedAssignment;
  }
}
