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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.importer.GraphicalModelImporter;
import com.github.thorbenlindhauer.importer.ImporterException;
import com.github.thorbenlindhauer.network.GraphicalModel;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * Imports models from the XML Bayesian Network Interchange format.
 * See <a href="https://www.cs.cmu.edu/~fgcozman/Research/InterchangeFormat/">spec</a>.
 *
 * @author Thorben
 *
 */
public class XMLBIFImporter implements GraphicalModelImporter<DiscreteFactor> {

  protected Map<String, XmlElementHandler> elementHandlers;

  public XMLBIFImporter() {
    elementHandlers = new HashMap<String, XmlElementHandler>();
    elementHandlers.put("network", new NetworkHandler());
    elementHandlers.put("variable", new VariableHandler());
    elementHandlers.put("definition", new DefinitionHandler());
  }

  public List<GraphicalModel<DiscreteFactor>> importFromStream(InputStream inputStream) {
    XMLBIFParse parse = new XMLBIFParse();

    XMLInputFactory factory = XMLInputFactory.newInstance();

    try {
      XMLEventReader reader = factory.createXMLEventReader(inputStream);

      while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();

        if (event.isStartElement()) {
          dispatch(event.asStartElement(), reader, parse);
        }
      }

    } catch (XMLStreamException e) {
      throw new ImporterException("Cannot import model", e);
    }

    List<GraphicalModel<DiscreteFactor>> graphicalModels = new ArrayList<GraphicalModel<DiscreteFactor>>();
    for (XMLBIFGraphicalModelParse modelParse : parse.getParses()) {
      GraphicalModel<DiscreteFactor> graphicalModel = new GraphicalModel<DiscreteFactor>(new Scope(modelParse.getVariables()), modelParse.getFactors());
      graphicalModels.add(graphicalModel);
    }

    return graphicalModels;
  }

  public void dispatch(StartElement element, XMLEventReader reader, XMLBIFParse parse) {
    XmlElementHandler handler = elementHandlers.get(element.getName().getLocalPart().toLowerCase());

    if (handler != null) {
      try {
        handler.process(this, reader, parse);
      } catch (XMLStreamException e) {
        throw new ImporterException("Could not handle element " + element, e);
      }
    }
  }

}
