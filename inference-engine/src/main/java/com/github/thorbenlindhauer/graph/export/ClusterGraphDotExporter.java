package com.github.thorbenlindhauer.graph.export;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.exception.ExportException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.graph.operation.DotExporter;

public class ClusterGraphDotExporter {

  protected String fileName;
  protected Map<Object, Integer> idMap = new HashMap<Object, Integer>();
  
  public ClusterGraphDotExporter(String fileName) {
    this.fileName = fileName;
  }
  
  public void writeToFile(ClusterGraph clusterGraph) {
    DotExporter exporter = new DotExporter();
    
    for (Cluster cluster : clusterGraph.getClusters()) {
      exporter.addNode(cluster);
      
      StringBuilder sb = new StringBuilder();
      sb.append("\"");
      sb.append(cluster.getScope());
      sb.append("\"");
      
      exporter.addNodeProperty(cluster, "label", sb.toString());
    }
    
    for (Edge edge : clusterGraph.getEdges()) {
      exporter.addEdge(edge.getCluster1(), edge.getCluster2());
    }
    
    FileWriter fileWriter = null;
    BufferedWriter buf = null;
    
    try {
      fileWriter = new FileWriter(fileName);
      buf = new BufferedWriter(fileWriter);
      exporter.writeTo(buf);
    } catch (IOException e) {
      throw new ExportException("Cannot write to file " + fileName, e);
    } finally {
      close(buf);
      close(fileWriter);
    }
  }
  
  protected void close(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      throw new ExportException("Cannot not close stream to file " + fileName, e);
    }
  }
  
  protected void writeEdge(Edge edge, BufferedWriter writer) throws IOException {
    Cluster cluster1 = edge.getCluster1();
    Cluster cluster2 = edge.getCluster2();
    
    StringBuilder edgeSb = new StringBuilder();
    edgeSb.append(getIdForObject(cluster1));
    edgeSb.append(" -- ");
    edgeSb.append(getIdForObject(cluster2));
    edgeSb.append(";\n");
    
    writer.write(edgeSb.toString());
  }
  
  protected void writeCluster(Cluster cluster, BufferedWriter writer) throws IOException {
    StringBuilder clusterSb = new StringBuilder();
    clusterSb.append(getIdForObject(cluster));
    clusterSb.append(" [label=\"");
    clusterSb.append(cluster.getScope().toString());
    clusterSb.append("\" ");
    
    clusterSb.append("factors=\"");
    for (DiscreteFactor factor : cluster.getFactors()) {
      clusterSb.append(factor.toString());
      clusterSb.append(" ");
    }
    clusterSb.append("\" ");
    
    clusterSb.append("];\n");
    
    writer.write(clusterSb.toString());
  }
  
  protected int getIdForObject(Object o) {
    Integer id = idMap.get(o);
    if (id == null) {
      id = System.identityHashCode(o);
      idMap.put(o, id);
    }
    
    return id;
  }
}
