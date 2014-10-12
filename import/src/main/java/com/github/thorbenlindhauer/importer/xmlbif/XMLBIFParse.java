package com.github.thorbenlindhauer.importer.xmlbif;

import java.util.ArrayList;
import java.util.List;

public class XMLBIFParse {

  protected List<XMLBIFGraphicalModelParse> graphicalModels;
  
  public XMLBIFParse() {
    this.graphicalModels = new ArrayList<XMLBIFGraphicalModelParse>();
  }
  
  public void newModel() {
    this.graphicalModels.add(new XMLBIFGraphicalModelParse());
  }
  
  public XMLBIFGraphicalModelParse getCurrentParse() {
    return graphicalModels.get(graphicalModels.size() - 1);
  }
  
  public List<XMLBIFGraphicalModelParse> getParses() {
    return graphicalModels;
  }
}
