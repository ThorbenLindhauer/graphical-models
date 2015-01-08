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
package com.github.thorbenlindhauer.inference.loopy;

import com.github.thorbenlindhauer.cluster.messagepassing.Message;
import com.github.thorbenlindhauer.cluster.messagepassing.MessageListener;
import com.github.thorbenlindhauer.factor.Factor;

/**
 * @author Thorben
 *
 */
public class MessageLogListener<T extends Factor<T>> implements MessageListener<T> {

  protected int messageCount;

  @Override
  public void notify(String eventName, Message<T> message) {
    StringBuilder logBuilder = new StringBuilder();

    logBuilder.append("Message ");
    logBuilder.append(messageCount++);
    logBuilder.append(": ");
    logBuilder.append(message.getSourceCluster());
    logBuilder.append(" => ");
    logBuilder.append(message.getTargetCluster());

    System.out.println(logBuilder.toString());

  }


}
