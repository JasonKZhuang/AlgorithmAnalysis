# AlgorithmAnalysis
This project uses KD-tree algorithm to create data structure

1 Objectives
  There are a number of key objectives for this assignment:
  a.Understand how a real problem can be implemented by different data structures and algorithms.
  b.Evaluate and contrast the performance of the data structures with respect to different usage scenarios and input data.
  In this assignment, we focus on the nearest neighbour problem.

2 Background
  Given a set of points, the nearest neighbour problem is about nding the nearest points to a query
  point. It appears in many different applications. One of these is facility search, e.g., querying what
  are the nearest restaurants or parks on your mobile phone. Another is identifying automatically
  recognising hand written digits on envelopes. In this assignment, we will focus on nearest neighbour
  problem for a spatial context, implement several well known data structures and algorithms for nding
  the nearest neighbours and comparing their performance. One of these data structures is the Kd-tree,
  which is a specialised binary tree and described in the following.

Kd-Trees
  Kd-trees is a binary tree data structure that enables more efficient nearest neighbour searches for 
  spatial queries. It builds an index to quickly search for nearest neighbours. Based on the set of points
  to index, Kd-trees recursively partition a multi-dimensional space, and generally ensures a balanced
  binary tree (making search relatively fast). In this assignment we focus on 2D spaces (something
  we are familar with and can be visualise). In the 2D space, we have x (horizonal) and y (vertical)
  dimensions.
