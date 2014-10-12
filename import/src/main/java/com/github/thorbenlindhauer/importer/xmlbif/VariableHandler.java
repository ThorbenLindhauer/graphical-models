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
