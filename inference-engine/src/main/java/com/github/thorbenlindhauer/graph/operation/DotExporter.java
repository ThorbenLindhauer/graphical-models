package com.github.thorbenlindhauer.graph.operation;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thorbenlindhauer.exception.ExportException;

public class DotExporter {

  protected Map<Object, DotNode> nodes;
  protected List<DotEdge> edges;
  
  public DotExporter() {
    this.nodes = new HashMap<Object, DotNode>();
    this.edges = new ArrayList<DotEdge>();
  }
  
  public void addNode(Object o) {
    if (!nodes.containsKey(o)) {
      nodes.put(o, new DotNode(o));
    }
  }
  
  public void addNodeProperty(Object o, String property, Object value) {
    DotNode node = nodes.get(o);
    
    if (node == null) {
      throw new ExportException("Unknown node: " + o);
    }
    
    node.addProperty(property, value);
  }
  
  public void addEdge(Object o1, Object o2) {
    if (nodes.containsKey(o1) && nodes.containsKey(o2)) {
      this.edges.add(new DotEdge(nodes.get(o1).id, nodes.get(o2).id));
    } else {
      throw new ExportException("Unknown nodes: " + o1 + ", " + o2);
    }
  }
  
  public void writeTo(Writer writer) throws IOException {
    writer.write("graph {");
    
    for (DotNode node : nodes.values()) {
      node.writeTo(writer);
    }
    
    for (DotEdge edge : edges) {
      edge.writeTo(writer);
    }
    
    writer.write("}");
  }
  
  public static class DotNode {
    protected Object value;
    protected int id;
    protected Map<String, Object> properties;
    
    public DotNode(Object value) {
      this.value = value;
      this.id = getIdForObject(value);
      this.properties = new HashMap<String, Object>();
    }
    
    public void addProperty(String property, Object value) {
      properties.put(property, value);
    }
    
    public void writeTo(Writer writer) throws IOException {
      StringBuilder sb = new StringBuilder();
      sb.append(id);
      sb.append(" [");
      
      for (Map.Entry<String, Object> property : properties.entrySet()) {
        sb.append(property.getKey());
        sb.append("=");
        sb.append(property.getValue());
        sb.append(" ");
      }
      
      sb.append("];\n");
      
      writer.write(sb.toString());
    }
  }
  
  public static class DotEdge {
    protected int node1Id;
    protected int node2Id;
    
    public DotEdge(int node1Id, int node2Id) {
      this.node1Id = node1Id;
      this.node2Id = node2Id;
    }
    
    public void writeTo(Writer writer) throws IOException {
      StringBuilder sb = new StringBuilder();
      sb.append(node1Id);
      sb.append(" -- ");
      sb.append(node2Id);
      sb.append(";\n");
      
      writer.write(sb.toString());
    }
  }
  
  protected static int getIdForObject(Object value) {
    return System.identityHashCode(value);
  }
}