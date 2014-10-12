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
public class XMLBIFImporter implements GraphicalModelImporter {
  
  protected Map<String, XmlElementHandler> elementHandlers;
  
  public XMLBIFImporter() {
    elementHandlers = new HashMap<String, XmlElementHandler>();
    elementHandlers.put("network", new NetworkHandler());
    elementHandlers.put("variable", new VariableHandler());
    elementHandlers.put("definition", new DefinitionHandler());
  }

  public List<GraphicalModel> importFromStream(InputStream inputStream) {
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
    
    List<GraphicalModel> graphicalModels = new ArrayList<GraphicalModel>();
    for (XMLBIFGraphicalModelParse modelParse : parse.getParses()) {
      GraphicalModel graphicalModel = new GraphicalModel(new Scope(modelParse.getVariables()), modelParse.getFactors());
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
