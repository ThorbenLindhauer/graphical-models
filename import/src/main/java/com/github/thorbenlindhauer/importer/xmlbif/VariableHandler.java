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
package com.github.thorbenlindhauer.importer.xmlbif;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.github.thorbenlindhauer.variable.DiscreteVariable;

public class VariableHandler implements XmlElementHandler {

  public void process(XMLBIFImporter importer, XMLEventReader reader, XMLBIFParse parse) throws XMLStreamException {
    boolean endEventSeen = false;
    
    String variableId = null;
    int variableCardinality = 0;
    
    while (!endEventSeen && reader.hasNext()) {
      XMLEvent nextEvent = reader.nextEvent();
      
      if (nextEvent.isStartElement()) {
        StartElement startElement = nextEvent.asStartElement();
        String startElementName = startElement.getName().getLocalPart();
        if (startElementName.equalsIgnoreCase("name")) {
          variableId = reader.nextEvent().asCharacters().getData();
        } else if (startElementName.equalsIgnoreCase("outcome")) {
          variableCardinality++;
        }
      } else if (nextEvent.isEndElement() && nextEvent.asEndElement().getName().getLocalPart().equalsIgnoreCase("variable")) {
        endEventSeen = true;
      }
    }
    
    parse.getCurrentParse().addVariable(new DiscreteVariable(variableId, variableCardinality));
    
  }

}
