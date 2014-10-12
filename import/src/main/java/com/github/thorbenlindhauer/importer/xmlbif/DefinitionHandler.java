package com.github.thorbenlindhauer.importer.xmlbif;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.IndexCoder;
import com.github.thorbenlindhauer.variable.IndexMapper;
import com.github.thorbenlindhauer.variable.Scope;

public class DefinitionHandler implements XmlElementHandler {

  public void process(XMLBIFImporter importer, XMLEventReader reader, XMLBIFParse parse) throws XMLStreamException {
    boolean endEventSeen = false;
    
    String forVariable = null;
    List<String> givenVariables = new ArrayList<String>();
    double[] valuesAsInInput = null;
    
    while (!endEventSeen && reader.hasNext()) {
      XMLEvent nextEvent = reader.nextEvent();
      
      if (nextEvent.isStartElement()) {
        StartElement startElement = nextEvent.asStartElement();
        String startElementName = startElement.getName().getLocalPart();
        
        if (startElementName.equalsIgnoreCase("for")) {
          forVariable = reader.nextEvent().asCharacters().getData();
        } else if ( startElementName.equalsIgnoreCase("given")) {
          givenVariables.add(reader.nextEvent().asCharacters().getData());
        } else if (startElementName.equalsIgnoreCase("table")) {
          String[] table = reader.nextEvent().asCharacters().getData().split(" ");
          valuesAsInInput = new double[table.length];
          
          for (int i = 0; i < valuesAsInInput.length; i++) {
            valuesAsInInput[i] = Double.parseDouble(table[i]);
          }
        }
        
      } else if (nextEvent.isEndElement() && nextEvent.asEndElement().getName().getLocalPart().equalsIgnoreCase("definition")) {
        endEventSeen = true;
      }
    }
    
    List<String> allVariables = new ArrayList<String>();
    allVariables.add(forVariable);
    
    // add given variables in reverse order which is required for mapping
    for (int i = givenVariables.size() - 1; i >= 0; i--) {
      allVariables.add(givenVariables.get(i));
    }
    
    Scope scope = parse.getCurrentParse().scopeFor(allVariables.toArray(new String[]{}));
    
    int[] variableMapping = new int[allVariables.size()];
    
    List<String> orderedVariableIds = new ArrayList<String>(scope.getVariableIds());
    for (int i = 0; i < variableMapping.length; i++) {
      int mappedIndex = allVariables.indexOf(orderedVariableIds.get(i));
      variableMapping[i] = mappedIndex;
    }
    
    IndexCoder indexCoder = scope.getIndexCoder();
    IndexMapper mapper = indexCoder.getIndexMapper(variableMapping);

    double[] reorderedValues = new double[valuesAsInInput.length];
    for (int i = 0; i < valuesAsInInput.length; i++) {
      int mappedIndex = mapper.reverseMapIndex(i);
      reorderedValues[mappedIndex] = valuesAsInInput[i];
    }
    
    parse.getCurrentParse().addFactor(new TableBasedDiscreteFactor(scope, reorderedValues));
    
  }

}
