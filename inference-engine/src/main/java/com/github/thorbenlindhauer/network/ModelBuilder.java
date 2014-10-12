package com.github.thorbenlindhauer.network;

public interface ModelBuilder {

  FactorBuilder factor();
  
  GraphicalModel build();
}
