package com.github.thorbenlindhauer.cluster.messagepassing;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

public interface MessagePassingContext {

  Message getMessage(Edge edge, Cluster sourceCluster);
  
  DiscreteFactor getClusterPotential(Cluster cluster);
  
  DiscreteFactor getJointDistribution(Cluster cluster);
  
  void updateClusterPotential(Cluster cluster, DiscreteFactor factor);
}
