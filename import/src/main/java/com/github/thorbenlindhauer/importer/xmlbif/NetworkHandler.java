package com.github.thorbenlindhauer.importer.xmlbif;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class NetworkHandler implements XmlElementHandler {

  public void process(XMLBIFImporter importer, XMLEventReader reader, XMLBIFParse parse) throws XMLStreamException {
    parse.newModel();
    
    boolean endEventSeen = false;
    
    while (!endEventSeen && reader.hasNext()) {
      XMLEvent nextEvent = reader.nextEvent();
      
      if (nextEvent.isStartElement()) {
        importer.dispatch(nextEvent.asStartElement(), reader, parse);
      } else if (nextEvent.isEndElement()) {
        endEventSeen = true;
      }
    }
    
  }

}
