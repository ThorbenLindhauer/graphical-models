graphical-models
================

*graphical-models* is a Java library of exact and approximate inference algorithms in Bayesian and Markov networks. 

Inference can be performed in networks with factors of types:

* Discrete factors
* Canonical Gaussian factors (including Gaussiand and conditional linear Gaussian distributions)

It provides implementations of the following algorithms:

* Variable Elimination 
* Clique Tree Inference
* Loopy Belief Propagation
* Expectation Propagation

Networks are specified in terms of cluster graphs. A cluster, a node in the cluster graph, may consist of any number of factors.

Variable Elimination
--------------------

The strategy for determining the elimination order can be exchanged. The following default implementations exist:

* Min-fill strategy: Eliminate variables in the order that introduces the least number of fill edges to the factor graph

Clique Tree Inference
---------------------

Implementations of the sum product and the belief update schema exist. Clique trees can be generated for a given factor graph based on a variable elimination strategy.

Loopy Belief Propagation
------------------------

Strategies for defining message order and determining calibration in the graph can be exchanged.

Expectation Propagation
-----------------------

Expectation propagation is complementary to clique tree inference and loopy belief propagation. It serves as a mean for approximation when factor products cannot be performed in closed form. The strategy of approximating a set of factors that form a cluster is configurable and extensible. The following implementations exist:

* Univariate truncated Gaussians can be approximated in Gaussian networks

Defining Models
---------------

A fluent builder for defining factors, grouping factors to clusters, and creating a graph out of them exist. A rudimentary import for graphs specified in the XMLBIF format is provided in addition.

Reading
-------

This library based on the concepts described in the book *Probabilistic Graphical Models* by Daphne Koller and Nir Friedmann:

Koller, Daphne, and Nir Friedman
*Probabilistic graphical models: principles and techniques*
MIT press, 2009
