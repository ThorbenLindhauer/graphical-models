package com.github.thorbenlindhauer.importer.xmlbif;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public interface XmlElementHandler {

  void process(XMLBIFImporter importer, XMLEventReader reader, XMLBIFParse parse) throws XMLStreamException;
}
