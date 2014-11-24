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
package com.github.thorbenlindhauer.graph.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UnionFindStructure<T> {

  protected Map<T, UnionFindPartition<T>> partitions;
  protected int numPartitions;
  
  public UnionFindStructure(Collection<T> initialElements) {
    partitions = new HashMap<T, UnionFindPartition<T>>();
    addSingletonPartitions(initialElements);
    numPartitions = partitions.keySet().size();
  }
  
  protected void addSingletonPartitions(Collection<T> elements) {
    for (T element : elements) {
      UnionFindPartition<T> partition = new UnionFindPartition<T>();
      partition.add(element);
      partitions.put(element, partition);
    }
  }
  
  public UnionFindPartition<T> getPartition(T element) {
    return partitions.get(element);
  }
  
  public void union(T element1, T element2) {
    UnionFindPartition<T> element1Partition = partitions.get(element1);
    UnionFindPartition<T> element2Partition = partitions.get(element2);
    
    if (element1Partition != element2Partition) {
      UnionFindPartition<T> unionPartition = element1Partition;
      // union is an inplace operation so no update for element1Partition elements
      unionPartition.union(element2Partition);
      
      for (T element : element2Partition.getElements()) {
        partitions.put(element, unionPartition);
      }
      
      numPartitions--;
    }
  }
  
  public int getNumPartitions() {
    return numPartitions;
  }
  
}
