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
package com.github.thorbenlindhauer.factorgraph;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.Factor;

public class FactorGraphEdge<T extends Factor<T>> {

  protected FactorGraphNode<T> node1;
  protected FactorGraphNode<T> node2;
  protected DiscreteFactor factor;

  public FactorGraphEdge(FactorGraphNode<T> node1, FactorGraphNode<T> node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  public FactorGraphNode<T> getConnectedNode(FactorGraphNode<T> source) {
    if (source == node1) {
      return node2;
    } else if (source == node2) {
      return node1;
    } else {
      throw new ModelStructureException("This edge does not connect node " + source);
    }
  }

  public void setFactor(DiscreteFactor factor) {
    this.factor = factor;
  }

  /**
   * The factor that this edge represents. One factor can be represented by multiple edges.
   * The factor may be null if the edge is a fill edge.
   * @return
   */
  public DiscreteFactor getFactor() {
    return factor;
  }

  public boolean connects(FactorGraphNode<T> node) {
    return node1 == node || node2 == node;
  }

}
