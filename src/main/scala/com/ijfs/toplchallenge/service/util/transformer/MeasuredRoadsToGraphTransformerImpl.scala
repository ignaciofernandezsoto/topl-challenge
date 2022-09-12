package com.ijfs.toplchallenge.service.util.transformer

import com.ijfs.toplchallenge.service.model.Intersection
import com.ijfs.toplchallenge.service.util.dijkstra.model.{Distance, Graph, WeightedArc}
import com.ijfs.toplchallenge.service.util.time.model.MeasuredRoad

class MeasuredRoadsToGraphTransformerImpl extends MeasuredRoadsToGraphTransformer {

  override def transform(measuredRoads: Set[MeasuredRoad]): Graph[Intersection] =
    measuredRoads.map {
      case MeasuredRoad(startAvenue, startStreet, averageTransitTime, endAvenue, endStreet) =>
        (
          Intersection(startAvenue, startStreet),
          Intersection(endAvenue, endStreet),
          averageTransitTime
        )
    }.foldLeft(Map[Intersection, Set[WeightedArc[Intersection]]]()) {

      case (graph, (initialIntersection, endIntersection, distance)) =>
        graph.get(initialIntersection) match
          case Some(weightedArcs) => graph.updated(initialIntersection, weightedArcs + WeightedArc(endIntersection, distance))
          case None => graph.updated(initialIntersection, Set(WeightedArc(endIntersection, distance)))

    }

}
