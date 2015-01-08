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
package com.github.thorbenlindhauer.cluster;

import org.assertj.core.api.Condition;

import com.github.thorbenlindhauer.factor.Factor;

public class ClusterEdgeCondition<T extends Factor<T>> extends Condition<Edge<T>> {

  protected Cluster<T> cluster1;
  protected Cluster<T> cluster2;

  public ClusterEdgeCondition(Cluster<T> cluster1, Cluster<T> cluster2) {
    this.cluster1 = cluster1;
    this.cluster2 = cluster2;
  }

  @Override
  public boolean matches(Edge<T> edge) {
    return edge.connects(cluster1) && edge.connects(cluster2);
  }
}
