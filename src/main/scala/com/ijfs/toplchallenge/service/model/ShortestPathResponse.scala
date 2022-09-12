package com.ijfs.toplchallenge.service.model

import com.ijfs.toplchallenge.service.util.dijkstra.model.Path

case class ShortestPathResponse(
                                 transitsAverageStrategy: AverageStrategy,
                                 startingIntersection: Intersection,
                                 endingIntersection: Intersection,
                                 paths: List[Path[Intersection]],
                                 totalTransitTime: Double,
                               )

enum AverageStrategy:
  case AVERAGE
  case WEIGHTED_AVERAGE
