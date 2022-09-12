package com.ijfs.toplchallenge.service.util.dijkstra.model

type Distance = Double

type Graph[Node] = Map[Node, Set[WeightedArc[Node]]]

case class WeightedArc[Node](
                              node: Node,
                              weight: Distance,
                            )

case class Path[Node](
                       initialNode: Node,
                       finalNode: Node,
                       distance: Distance
                     )
