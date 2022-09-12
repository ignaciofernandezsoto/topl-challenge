package com.ijfs.toplchallenge.service

import cats.effect.Async
import com.ijfs.toplchallenge.service.model.{ShortestPathRequest, Intersection, ShortestPathResponse}
import com.ijfs.toplchallenge.service.reader.TrafficFlowReaderService
import com.ijfs.toplchallenge.service.util.dijkstra.DijkstraAlgorithm
import com.ijfs.toplchallenge.service.util.dijkstra.model.Path
import com.ijfs.toplchallenge.service.util.grouping.RoadsGrouper
import com.ijfs.toplchallenge.service.util.time.TransitTimeResolver
import com.ijfs.toplchallenge.service.util.transformer.MeasuredRoadsToGraphTransformer

trait ShortestPathSolver[F[_] : Async]:
  def solve(request: ShortestPathRequest): F[Either[String, ShortestPathResponse]]

object ShortestPathSolver:
  def apply[F[_] : Async]: ShortestPathSolver[F] =
    new ShortestPathSolverImpl[F](
      TrafficFlowReaderService.apply[F],
      RoadsGrouper.apply,
      TransitTimeResolver.apply,
      MeasuredRoadsToGraphTransformer.apply,
      DijkstraAlgorithm.apply,
    )