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

import java.util.List;

import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.Message;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContext;
import com.github.thorbenlindhauer.factor.GaussianFactor;
import com.github.thorbenlindhauer.inference.loopy.ClusterGraphCalibrationContext;
import com.github.thorbenlindhauer.inference.loopy.ClusterGraphCalibrationContextFactory;

/**
 * @author Thorben
 *
 */
public class StaticCalibrationContextFactory implements ClusterGraphCalibrationContextFactory<GaussianFactor> {

  protected List<MessageInstruction> instructions;

  public StaticCalibrationContextFactory(List<MessageInstruction> instructions) {
    this.instructions = instructions;
  }

  public ClusterGraphCalibrationContext<GaussianFactor> buildCalibrationContext(ClusterGraph<GaussianFactor> clusterGraph,
      final MessagePassingContext<GaussianFactor> messagePassingContext) {

    return new ClusterGraphCalibrationContext<GaussianFactor>() {

      protected int messageCounter = 0;

      public void notify(String eventName, Message<GaussianFactor> object) {
      }

      public Message<GaussianFactor> getNextUncalibratedMessage() {
        if (messageCounter >= instructions.size()) {
          return null;
        }

        MessageInstruction instruction = instructions.get(messageCounter);
        Message<GaussianFactor> nextMessage = messagePassingContext.getMessage(instruction.getEdge(), instruction.getSourceCluster());
        messageCounter++;
        return nextMessage;
      }
    };
  }

}
