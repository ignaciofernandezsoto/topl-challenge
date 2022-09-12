package com.ijfs.toplchallenge.service.util.dijkstra.model

private[dijkstra] type DistancesToTheSource[Node] = Map[Node, PredecessorDistance[Node]]

private[dijkstra] case class PredecessorDistance[Node](distance: RelativeDistance, predecessor: Option[Node] = None)

private[dijkstra] case class RelativeDistance(predecessorDistance: Distance, sourceDistance: Distance)