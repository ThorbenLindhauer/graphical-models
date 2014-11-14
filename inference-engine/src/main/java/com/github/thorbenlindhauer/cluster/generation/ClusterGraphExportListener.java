package com.github.thorbenlindhauer.cluster.generation;

import com.github.thorbenlindhauer.Listener;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.graph.export.ClusterGraphDotExporter;

public class ClusterGraphExportListener implements Listener<ClusterGraph> {

  protected String fileName;
  
  public ClusterGraphExportListener(String fileName) {
    this.fileName = fileName;
  }
  
  @Override
  public void notify(String eventName, ClusterGraph clusterGraph) {
    ClusterGraphDotExporter exporter = new ClusterGraphDotExporter(fileName);
    exporter.writeToFile(clusterGraph);    
  }

}
