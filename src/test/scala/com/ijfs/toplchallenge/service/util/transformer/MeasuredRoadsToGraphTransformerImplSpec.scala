package com.ijfs.toplchallenge.service.util.transformer

import com.ijfs.toplchallenge.service.model.Intersection
import com.ijfs.toplchallenge.service.util.dijkstra.model.WeightedArc
import com.ijfs.toplchallenge.service.util.time.model.MeasuredRoad
import com.ijfs.toplchallenge.service.util.transformer.MeasuredRoadsToGraphTransformerImplSpec.*
import com.ijfs.toplchallenge.service.util.transformer.{MeasuredRoadsToGraphTransformer, MeasuredRoadsToGraphTransformerImpl}
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

class MeasuredRoadsToGraphTransformerImplSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  val transformer: MeasuredRoadsToGraphTransformer = new MeasuredRoadsToGraphTransformerImpl

  Feature("transform") {

    Scenario("On an empty set of measured roads, an empty graph should be returned") {

      Given("an empty set of measured roads")
      val measuredRoads: Set[MeasuredRoad] = Set.empty

      When("transformer is called")
      val result = transformer.transform(measuredRoads)

      Then("an empty graph should be returned")
      result must be(empty)

    }

    Scenario("On a single element of measured roads, a graph with a two intersection should be returned") {

      Given("an empty set of measured roads")
      val measuredRoads: Set[MeasuredRoad] = Set(
        MeasuredRoad(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          averageTransitTime = TRANSIT_TIME_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        )
      )

      When("transformer is called")
      val result = transformer.transform(measuredRoads)

      Then("an empty graph should be returned")
      result must (have size 1 and equal(
        Map(
          Intersection(avenue = START_AVENUE_01, street = START_STREET_01) -> Set(
            WeightedArc[Intersection](
              node = Intersection(avenue = END_AVENUE_01, street = END_STREET_01),
              weight = TRANSIT_TIME_01,
            )
          )
        )
      ))

    }

    Scenario("On two separate elements of measured roads, a graph with four intersections should be returned") {

      Given("an empty set of measured roads")
      val measuredRoads: Set[MeasuredRoad] = Set(
        MeasuredRoad(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          averageTransitTime = TRANSIT_TIME_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        ),
        MeasuredRoad(
          startAvenue = START_AVENUE_02,
          startStreet = START_STREET_02,
          averageTransitTime = TRANSIT_TIME_02,
          endAvenue = END_AVENUE_02,
          endStreet = END_STREET_02,
        ),
      )

      When("transformer is called")
      val result = transformer.transform(measuredRoads)

      Then("an empty graph should be returned")
      result must (have size 2 and equal(
        Map(
          Intersection(avenue = START_AVENUE_01, street = START_STREET_01) -> Set(
            WeightedArc[Intersection](
              node = Intersection(avenue = END_AVENUE_01, street = END_STREET_01),
              weight = TRANSIT_TIME_01,
            )
          ),
          Intersection(avenue = START_AVENUE_02, street = START_STREET_02) -> Set(
            WeightedArc[Intersection](
              node = Intersection(avenue = END_AVENUE_02, street = END_STREET_02),
              weight = TRANSIT_TIME_02,
            )
          ),
        )
      ))

    }

    Scenario("On three elements of measured roads with the first and last sharing the starting avenue, a graph with five intersections and two of them being and arc of the other should be returned") {

      Given("an empty set of measured roads")
      val measuredRoads: Set[MeasuredRoad] = Set(
        MeasuredRoad(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          averageTransitTime = TRANSIT_TIME_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        ),
        MeasuredRoad(
          startAvenue = START_AVENUE_02,
          startStreet = START_STREET_02,
          averageTransitTime = TRANSIT_TIME_02,
          endAvenue = END_AVENUE_02,
          endStreet = END_STREET_02,
        ),
        MeasuredRoad(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          averageTransitTime = TRANSIT_TIME_03,
          endAvenue = END_AVENUE_02,
          endStreet = END_STREET_02,
        ),
      )

      When("transformer is called")
      val result = transformer.transform(measuredRoads)

      Then("an empty graph should be returned")
      result must (have size 2 and equal(
        Map(
          Intersection(avenue = START_AVENUE_01, street = START_STREET_01) -> Set(
            WeightedArc[Intersection](
              node = Intersection(avenue = END_AVENUE_01, street = END_STREET_01),
              weight = TRANSIT_TIME_01,
            ),
            WeightedArc[Intersection](
              node = Intersection(avenue = END_AVENUE_02, street = END_STREET_02),
              weight = TRANSIT_TIME_03,
            ),
          ),
          Intersection(avenue = START_AVENUE_02, street = START_STREET_02) -> Set(
            WeightedArc[Intersection](
              node = Intersection(avenue = END_AVENUE_02, street = END_STREET_02),
              weight = TRANSIT_TIME_02,
            )
          ),
        )
      ))

    }

  }

}

object MeasuredRoadsToGraphTransformerImplSpec {

  private final val START_AVENUE_01 = "START_AVENUE_01"
  private final val START_STREET_01 = "START_STREET_01"
  private final val END_AVENUE_01 = "END_AVENUE_01"
  private final val END_STREET_01 = "END_STREET_01"

  private final val START_AVENUE_02 = "START_AVENUE_02"
  private final val START_STREET_02 = "START_STREET_02"
  private final val END_AVENUE_02 = "END_AVENUE_02"
  private final val END_STREET_02 = "END_STREET_02"

  private final val TRANSIT_TIME_01 = 20d
  private final val TRANSIT_TIME_02 = 200d
  private final val TRANSIT_TIME_03 = 2000d

}