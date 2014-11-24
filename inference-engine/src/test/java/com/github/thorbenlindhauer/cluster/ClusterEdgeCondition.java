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

public class ClusterEdgeCondition extends Condition<Edge> {

  protected Cluster cluster1;
  protected Cluster cluster2;
  
  public ClusterEdgeCondition(Cluster cluster1, Cluster cluster2) {
    this.cluster1 = cluster1;
    this.cluster2 = cluster2;
  }
  
  @Override
  public boolean matches(Edge edge) {
    return edge.connects(cluster1) && edge.connects(cluster2);
  }
}
