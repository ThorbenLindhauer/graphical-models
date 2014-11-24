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
package com.github.thorbenlindhauer.inference.variableelimination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.thorbenlindhauer.exception.InferenceException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Implements the min fill variable elimination strategy that tries to eliminate variables
 * by introducing the least number of new fill edges.
 * 
 * @author Thorben
 */
public class MinFillEliminationStrategy implements VariableEliminationStrategy {

  public List<String> getEliminationOrder(GraphicalModel graphicalModel, Collection<String> variablesToEliminate) {
    List<String> eliminationOrder = new ArrayList<String>();
    
    // map: variable id => all variable ids that are in a common factor
    Map<String, Set<String>> connectedVariables = new HashMap<String, Set<String>>();
    for (DiscreteFactor factor : graphicalModel.getFactors()) {
      Scope scope = factor.getVariables();
      
      for (String variableId : scope.getVariableIds()) {
        Set<String> connectedVariablesForCurrent = connectedVariables.get(variableId);
        if (connectedVariablesForCurrent == null) {
          connectedVariablesForCurrent = new HashSet<String>();
          connectedVariables.put(variableId, connectedVariablesForCurrent);
        }
        
        connectedVariablesForCurrent.addAll(Arrays.asList(scope.getVariableIds()));
        connectedVariablesForCurrent.remove(variableId);
      }
    }
    
    Set<String> candidatesLeft = new HashSet<String>(variablesToEliminate);
    while (!candidatesLeft.isEmpty()) {
      // find next variable
      String nextEliminationVariable = null;
      int newFillEdges = Integer.MAX_VALUE;
      for (String candidateVariable : candidatesLeft) {
        int newFillEdgesForCandidate = 0;
        
        for (String connectedVariable : connectedVariables.get(candidateVariable)) {
          // contains all variables that are currently not connected to connectedVariable but would be afterwards
          Set<String> variablesToBeMarried = new HashSet<String>(connectedVariables.get(candidateVariable));
          variablesToBeMarried.removeAll(connectedVariables.get(connectedVariable));
          
          newFillEdgesForCandidate += variablesToBeMarried.size();
        }
        
        // hint: correct number of new fill edges would be newFillEdgesForCandidate / 2 since the above logic counts each edge twice
        
        if (newFillEdgesForCandidate < newFillEdges) {
          nextEliminationVariable = candidateVariable;
          newFillEdges = newFillEdgesForCandidate;
        }
      }
      
      if (nextEliminationVariable == null) {
        throw new InferenceException("Could not determine next variable to eliminate");
      }
      
      eliminationOrder.add(nextEliminationVariable);
      
      // remove the next eliminated variable from the set of candidates
      candidatesLeft.remove(nextEliminationVariable);
      Set<String> variablesToBeMarried = connectedVariables.remove(nextEliminationVariable);
      
      // "marry" the variables that are now supposed to share a common factor
      for (String variableToMarry : variablesToBeMarried) {
        connectedVariables.get(variableToMarry).addAll(variablesToBeMarried);
        connectedVariables.get(variableToMarry).remove(nextEliminationVariable);
        connectedVariables.get(variableToMarry).remove(variableToMarry);
      }
      
    }
    
    
    return eliminationOrder;
  }

}
