package com.ijfs.toplchallenge.service

import cats.Applicative
import cats.effect.Async
import cats.syntax.all.*
import com.ijfs.toplchallenge.service.model.{Intersection, ShortestPathRequest, ShortestPathRequestWithAverageTimes, ShortestPathRequestWithWeightedTimes, ShortestPathResponse, AverageStrategy}
import com.ijfs.toplchallenge.service.reader.TrafficFlowReaderService
import com.ijfs.toplchallenge.service.reader.model.TrafficFlowDto
import com.ijfs.toplchallenge.service.util.dijkstra.DijkstraAlgorithm
import com.ijfs.toplchallenge.service.util.dijkstra.model.Path
import com.ijfs.toplchallenge.service.util.grouping.RoadsGrouper
import com.ijfs.toplchallenge.service.util.time.TransitTimeResolver
import com.ijfs.toplchallenge.service.util.transformer.MeasuredRoadsToGraphTransformer

import scala.language.postfixOps
import scala.util.{Failure, Success}

class ShortestPathSolverImpl[F[_] : Async](
                                            trafficFlowReaderService: TrafficFlowReaderService[F],
                                            roadsGrouper: RoadsGrouper,
                                            transitTimeResolver: TransitTimeResolver,
                                            graphTransformer: MeasuredRoadsToGraphTransformer,
                                            dijkstra: DijkstraAlgorithm,

                                          )
  extends ShortestPathSolver[F] {

  override def solve(request: ShortestPathRequest): F[Either[String, ShortestPathResponse]] =
    trafficFlowReaderService.read(request.filePath)
      .map {
        case Left(e) => Left(s"Unexpected error while reading error=[$e]")
        case Right(trafficFlow: TrafficFlowDto) =>
          println("Group by Measured Roads")
          val groupedRoads = roadsGrouper.group(trafficFlow)

          println("Set the average trafficTime")
          val measuredRoads = transitTimeResolver.resolve(groupedRoads, request)

          println("Build the graph")
          val graph = graphTransformer.transform(measuredRoads)

          println("Apply the shortest path algorithm")
          dijkstra.shortestPath(graph, request.initialIntersection, request.finalIntersection) match
            case Left(e) => Left(s"An error occurred while searching for the shortest path error=[$e]")
            case Right(paths) => Right(
              ShortestPathResponse(
                transitsAverageStrategy = request match
                  case _: ShortestPathRequestWithAverageTimes => AverageStrategy.AVERAGE
                  case _: ShortestPathRequestWithWeightedTimes => AverageStrategy.WEIGHTED_AVERAGE,
                startingIntersection = request.initialIntersection,
                endingIntersection = request.finalIntersection,
                paths = paths,
                totalTransitTime = paths.map(_.distance).sum
              )
            )
      }

}