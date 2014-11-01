package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;


public interface Message {

  void update(MessagePassingContext messagePassingContext);
  
  DiscreteFactor getPotential();
  
  Edge getEdge();
  
  Cluster getTargetCluster();
  
  Cluster getSourceCluster();
  
}
