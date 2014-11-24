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

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.variable.Scope;

public class Edge {

  protected Cluster cluster1;
  protected Cluster cluster2;
  
  protected Scope scope;
  
  public Edge(Cluster cluster1, Cluster cluster2) {
    this.cluster1 = cluster1;
    this.cluster2 = cluster2;
    
    scope = cluster1.getScope().intersect(cluster2.getScope());
  }
  
  public Cluster getTarget(Cluster source) {
    if (source == cluster1) {
      return cluster2;
    } else if (source == cluster2) {
      return cluster1;
    } else {
      throw new ModelStructureException("Source cluster " + source + " is not part of this edge");
    }
  }
  
  public Cluster getCluster1() {
    return cluster1;
  }
  
  public Cluster getCluster2() {
    return cluster2;
  }
  
  public Scope getScope() {
    return scope;
  }
  
  public boolean connects(Cluster cluster) {
    return cluster1 == cluster || cluster2 == cluster;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    sb.append(cluster1.toString());
    sb.append(" => ");
    sb.append(cluster2.toString());
    
    return sb.toString();
  }
}
