package com.github.thorbenlindhauer.network;

public interface FactorBuilder {

  FactorBuilder scope(String... variableIds);
  
  ModelBuilder basedOnTable(double[] table);
}
