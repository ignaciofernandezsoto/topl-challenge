package com.ijfs.toplchallenge.service

import cats.Applicative
import cats.effect.unsafe.implicits.global
import cats.implicits.*
import cats.syntax.all.*
import com.ijfs.toplchallenge.service.ShortestPathSolverImplSpec.*
import com.ijfs.toplchallenge.service.error.Failures.ParseException
import com.ijfs.toplchallenge.service.error.ToplException
import com.ijfs.toplchallenge.service.model.{AverageStrategy, Intersection, ShortestPathRequest, ShortestPathRequestWithAverageTimes, ShortestPathRequestWithWeightedTimes, ShortestPathResponse}
import com.ijfs.toplchallenge.service.reader.TrafficFlowReaderService
import com.ijfs.toplchallenge.service.reader.model.TrafficFlowDto
import com.ijfs.toplchallenge.service.util.dijkstra.DijkstraAlgorithm
import com.ijfs.toplchallenge.service.util.dijkstra.model.{Graph, Path}
import com.ijfs.toplchallenge.service.util.grouping.RoadsGrouper
import com.ijfs.toplchallenge.service.util.grouping.model.GroupedMeasurementRoads
import com.ijfs.toplchallenge.service.util.time.TransitTimeResolver
import com.ijfs.toplchallenge.service.util.time.model.MeasuredRoad
import com.ijfs.toplchallenge.service.util.transformer.MeasuredRoadsToGraphTransformer
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

import scala.collection.immutable.Map
import scala.util.Right

class ShortestPathSolverImplSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  type IO[+A] = cats.effect.IO[A]

  Feature("solve") {

    Scenario("On fail traffic flow json read, the error should be bubbled up") {

      Given("fail traffic flow json read")
      val shortestPathSolver = new ShortestPathSolverImpl[IO](
        trafficFlowReaderService = new TrafficFlowReaderService[IO] {
          override def read(fileName: String): IO[Either[ToplException, TrafficFlowDto]] = Left(EXCEPTION).pure[IO]
        },
        roadsGrouper = (trafficFlow: TrafficFlowDto) => ???,
        transitTimeResolver = (groupedMeasurementRoads: GroupedMeasurementRoads, bestPathRequest: ShortestPathRequest) => ???,
        graphTransformer = (measuredRoads: Set[MeasuredRoad]) => ???,
        dijkstra = new DijkstraAlgorithm {
          override def shortestPath[Node](graph: Graph[Node], source: Node, end: Node): Either[ToplException, List[Path[Node]]] = ???
        },
      )

      val request: ShortestPathRequest = ShortestPathRequestWithAverageTimes(INTERSECTION_01, INTERSECTION_02, FILE_PATH)

      When("shortest path solver is called")
      val result = shortestPathSolver.solve(request)

      Then("the error should be bubbled up")
      result.unsafeRunSync() must be(Left(s"Unexpected error while reading error=[$EXCEPTION]"))

    }

    Scenario("On fail Dijkstra algorithm execution, the error should be bubbled up") {

      Given("fail traffic flow json read")
      val shortestPathSolver = new ShortestPathSolverImpl[IO](
        trafficFlowReaderService = new TrafficFlowReaderService[IO] {
          override def read(fileName: String): IO[Either[ToplException, TrafficFlowDto]] = Right(TrafficFlowDto(Set.empty)).pure[IO]
        },
        roadsGrouper = (trafficFlow: TrafficFlowDto) => Map.empty,
        transitTimeResolver = (groupedMeasurementRoads: GroupedMeasurementRoads, bestPathRequest: ShortestPathRequest) => Set.empty,
        graphTransformer = (measuredRoads: Set[MeasuredRoad]) => Map.empty,
        dijkstra = new DijkstraAlgorithm {
          override def shortestPath[Node](graph: Graph[Node], source: Node, end: Node): Either[ToplException, List[Path[Node]]] = Left(EXCEPTION)
        },
      )

      val request: ShortestPathRequest = ShortestPathRequestWithAverageTimes(INTERSECTION_01, INTERSECTION_02, FILE_PATH)

      When("shortest path solver is called")
      val result = shortestPathSolver.solve(request)

      Then("the error should be bubbled up")
      result.unsafeRunSync() must be(Left(s"An error occurred while searching for the shortest path error=[$EXCEPTION]"))

    }

    Scenario("On successful Dijkstra algorithm execution with an average time request, the result should be returned with transit time average type average") {

      Given("successful Dijkstra algorithm execution with an average time request")
      val shortestPathSolver = new ShortestPathSolverImpl[IO](
        trafficFlowReaderService = new TrafficFlowReaderService[IO] {
          override def read(fileName: String): IO[Either[ToplException, TrafficFlowDto]] = Right(TrafficFlowDto(Set.empty)).pure[IO]
        },
        roadsGrouper = (trafficFlow: TrafficFlowDto) => Map.empty,
        transitTimeResolver = (groupedMeasurementRoads: GroupedMeasurementRoads, bestPathRequest: ShortestPathRequest) => Set.empty,
        graphTransformer = (measuredRoads: Set[MeasuredRoad]) => Map.empty,
        dijkstra = new DijkstraAlgorithm {
          override def shortestPath[Node](graph: Graph[Node], source: Node, end: Node): Either[ToplException, List[Path[Node]]] = Right(PATHS.asInstanceOf[List[Path[Node]]])
        },
      )

      val request: ShortestPathRequest = ShortestPathRequestWithAverageTimes(INTERSECTION_01, INTERSECTION_02, FILE_PATH)

      When("shortest path solver is called")
      val result = shortestPathSolver.solve(request)

      Then("the result should be returned with transit time average type average")
      result.unsafeRunSync() must be(Right(ShortestPathResponse(
        transitsAverageStrategy = AverageStrategy.AVERAGE,
        startingIntersection = INTERSECTION_01,
        endingIntersection = INTERSECTION_02,
        paths = PATHS,
        totalTransitTime = PATHS_TOTAL_DISTANCE,
      )))

    }

    Scenario("On successful Dijkstra algorithm execution with a weighted average time request, the result should be returned with transit time average type weighted average") {

      Given("successful Dijkstra algorithm execution with an average time request")
      val shortestPathSolver = new ShortestPathSolverImpl[IO](
        trafficFlowReaderService = new TrafficFlowReaderService[IO] {
          override def read(fileName: String): IO[Either[ToplException, TrafficFlowDto]] = Right(TrafficFlowDto(Set.empty)).pure[IO]
        },
        roadsGrouper = (trafficFlow: TrafficFlowDto) => Map.empty,
        transitTimeResolver = (groupedMeasurementRoads: GroupedMeasurementRoads, bestPathRequest: ShortestPathRequest) => Set.empty,
        graphTransformer = (measuredRoads: Set[MeasuredRoad]) => Map.empty,
        dijkstra = new DijkstraAlgorithm {
          override def shortestPath[Node](graph: Graph[Node], source: Node, end: Node): Either[ToplException, List[Path[Node]]] = Right(PATHS.asInstanceOf[List[Path[Node]]])
        },
      )

      val request: ShortestPathRequest = ShortestPathRequestWithWeightedTimes(INTERSECTION_01, INTERSECTION_02, FILE_PATH)

      When("shortest path solver is called")
      val result = shortestPathSolver.solve(request)

      Then("the result should be returned with transit time average type average")
      result.unsafeRunSync() must be(Right(ShortestPathResponse(
        transitsAverageStrategy = AverageStrategy.WEIGHTED_AVERAGE,
        startingIntersection = INTERSECTION_01,
        endingIntersection = INTERSECTION_02,
        paths = PATHS,
        totalTransitTime = PATHS_TOTAL_DISTANCE,
      )))

    }

  }

}

object ShortestPathSolverImplSpec {

  private final val FILE_PATH = "FILE_PATH"

  private final val INTERSECTION_01 = Intersection("A", "1")
  private final val INTERSECTION_02 = Intersection("Z", "9")
  private final val INTERSECTION_03 = Intersection("X", "2")
  private final val INTERSECTION_04 = Intersection("U", "9")
  private final val INTERSECTION_05 = Intersection("R", "2")
  private final val INTERSECTION_06 = Intersection("L", "3")

  private final val EXCEPTION = new ToplException("Test") {}

  private final val PATHS: List[Path[Intersection]] = Path[Intersection](INTERSECTION_01, INTERSECTION_02, 10) :: Path[Intersection](INTERSECTION_03, INTERSECTION_04, 2) :: Path[Intersection](INTERSECTION_05, INTERSECTION_06, 9) :: Nil
  private final val PATHS_TOTAL_DISTANCE: Double = 21

}
