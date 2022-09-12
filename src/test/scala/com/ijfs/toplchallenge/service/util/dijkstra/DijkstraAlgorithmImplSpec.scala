package com.ijfs.toplchallenge.service.util.dijkstra

import DijkstraAlgorithmImplSpec.*
import com.ijfs.toplchallenge.service.error.Failures.NonExistentNode
import com.ijfs.toplchallenge.service.error.ToplException
import com.ijfs.toplchallenge.service.util.dijkstra.DijkstraAlgorithmImpl
import com.ijfs.toplchallenge.service.util.dijkstra.model.{WeightedArc, *}
import io.github.arainko.ducktape.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

import scala.annotation.tailrec

class DijkstraAlgorithmImplSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  val dijkstraAlgorithm = new DijkstraAlgorithmImpl

  Feature("shortestPath") {
    Scenario("On a node being the source and end with an empty graph, a path from the source to the end with zero distance is returned") {
      Given("a node being the source and end with an empty graph")
      val graph = Map[String, Set[WeightedArc[String]]]()
      val source = NODE_A
      val end = source

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("a path from the source to the end with zero distance is returned")
      result must be(Right(
        Path[String](source, end, 0) :: Nil
      ))
    }

    Scenario("On a node source that has a first degree neighbour with the end node and distance one, and no other nodes on the graph, a single path from the source to the end with distance one is returned") {
      Given("a node source that has a first degree neighbour with another node and distance one, and no other nodes on the graph")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(WeightedArc(NODE_B, WEIGHT_01)),
        NODE_B -> Set()
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("a single path from the source to the end with distance one is returned")
      result must be(Right(
        Path[String](source, end, 1) :: Nil
      ))
    }

    Scenario("On a node source that has a first degree neighbour with the end node and distance one, and with other nodes on the graph, a single path from the source to the end with distance one is returned") {
      Given("a node source that has a first degree neighbour with another node and distance one, and with other nodes on the graph")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_B, WEIGHT_01),
          WeightedArc(NODE_C, WEIGHT_02),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(),
        NODE_D -> Set()
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("a single path from the source to the end with distance one is returned")
      result must be(Right(
        Path[String](source, end, 1) :: Nil
      ))
    }

    Scenario("On a node source that has a second degree neighbour with the end node and distance two and one respectively, and with other nodes on the graph, two paths from the source to the end with distance three are returned") {
      Given("a node source that has a first degree neighbour with another node and distance two and one respectively, and with other nodes on the graph")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_C, WEIGHT_02),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_B, WEIGHT_01),
        ),
        NODE_D -> Set()
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("two paths from the source to the end with distance three are returned")
      result must be(Right(
        Path(source, NODE_C, 2) :: Path(NODE_C, end, 1) :: Nil
      ))
    }

    Scenario("On a node source that has a third degree neighbour with the end node and distance two, four and one respectively, and with other nodes on the graph, three paths from the source to the end with distance seven are returned") {
      Given("a node source that has a third degree neighbour with the end node and distance two, four and one respectively, and with other nodes on the graph")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_C, WEIGHT_02),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_D, WEIGHT_04),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_B, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("three paths from the source to the end with distance seven are returned")
      result must be(Right(
        Path(source, NODE_C, 2) :: Path(NODE_C, NODE_D, 4) :: Path(NODE_D, end, 1) :: Nil
      ))
    }

    Scenario("On [(A-1>C), (A-2>D), (B-3>D), (C-4>B), (D-1>B)], [(A-2>D), (D-1>B)] should be returned") {
      Given("[(A-1>C), (A-2>D), (B-3>D), (C-4>B), (D-1>B)]")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_C, WEIGHT_01),
          WeightedArc(NODE_D, WEIGHT_02),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_B, WEIGHT_04),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_B, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("[(A-2>D), (D-1>B)] should be returned")
      result must be(Right(
        Path(source, NODE_D, 2) :: Path(NODE_D, end, 1) :: Nil
      ))
    }

    Scenario("On [(A-2>C), (A-4>D), (B-3>D), (C-4>B), (D-1>B)], [(A-4>D), (D-1>B)] should be returned") {
      Given("[(A-2>C), (A-4>D), (B-3>D), (C-4>B), (D-1>B)]")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_C, WEIGHT_02),
          WeightedArc(NODE_D, WEIGHT_04),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_D, WEIGHT_04),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_B, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("[(A-4>D), (D-1>B)] should be returned")
      result must be(Right(
        Path(source, NODE_D, 4) :: Path(NODE_D, end, 1) :: Nil
      ))
    }

    Scenario("On [(A-1>C), (A-1>D), (B-3>D), (C-4>B), (D-1>B)], [(A-1>D), (D-1>B)] should be returned") {
      Given("[(A-1>C), (A-1>D), (B-3>D), (C-4>B), (D-1>B)]")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_C, WEIGHT_01),
          WeightedArc(NODE_D, WEIGHT_01),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_D, WEIGHT_04),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_B, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("[(A-1>D), (D-1>B)] should be returned")
      result must be(Right(
        Path(source, NODE_D, 1) :: Path(NODE_D, end, 1) :: Nil
      ))
    }

    Scenario("On [(A-4>B), (A-1>C), (B-3>D), (C-1>D), (D-1>B)], [(A-1>C), (C-1>D), (D-1>B)] should be returned") {
      Given("[(A-4>B), (A-1>C), (B-3>D), (C-1>D), (D-1>B)]")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_B, WEIGHT_04),
          WeightedArc(NODE_C, WEIGHT_01),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_D, WEIGHT_01),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_B, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("[(A-1>C), (C-1>D), (D-1>B)] should be returned")
      result must be(Right(
        Path(source, NODE_C, 1) :: Path(NODE_C, NODE_D, 1) :: Path(NODE_D, end, 1) :: Nil
      ))
    }

    Scenario("On a node source that has no degree neighbour with the end node, and with other nodes on the graph, no paths are returned") {
      Given("a node source that has no degree neighbour with the end node, and with other nodes on the graph")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_C, WEIGHT_02),
        ),
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_D, WEIGHT_04),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_A, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("no paths are returned")
      result must be(Right(
        Nil
      ))
    }

    Scenario("On a graph with the source node not present, a Left NonExistentNode failure should be returned") {
      Given("a graph with the source node not present")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_B -> Set(
          WeightedArc(NODE_D, WEIGHT_03),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_D, WEIGHT_04),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_A, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("a Left NonExistentNode failure should be returned")
      result must be(Left(NonExistentNode(source)))
    }

    Scenario("On a graph with the end node not present, a Left NonExistentNode failure should be returned") {
      Given("a graph with the end node not present")
      val graph = Map[String, Set[WeightedArc[String]]](
        NODE_A -> Set(
          WeightedArc(NODE_C, WEIGHT_02),
        ),
        NODE_C -> Set(
          WeightedArc(NODE_D, WEIGHT_04),
        ),
        NODE_D -> Set(
          WeightedArc(NODE_A, WEIGHT_01),
        )
      )
      val source = NODE_A
      val end = NODE_B

      When("dijkstra is called")
      val result = dijkstraAlgorithm.shortestPath(graph, source, end)

      Then("a Left NonExistentNode failure should be returned")
      result must be(Left(NonExistentNode(end)))
    }

  }
}

object DijkstraAlgorithmImplSpec {

  private final val NODE_A = "A"
  private final val NODE_B = "B"
  private final val NODE_C = "C"
  private final val NODE_D = "D"

  private final val WEIGHT_01 = 1
  private final val WEIGHT_02 = 2
  private final val WEIGHT_03 = 3
  private final val WEIGHT_04 = 4

}