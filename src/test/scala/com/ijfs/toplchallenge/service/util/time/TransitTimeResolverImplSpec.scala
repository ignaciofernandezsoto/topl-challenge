package com.ijfs.toplchallenge.service.util.time

import com.ijfs.toplchallenge.service.model.{ShortestPathRequest, ShortestPathRequestWithAverageTimes, ShortestPathRequestWithWeightedTimes, Intersection}
import com.ijfs.toplchallenge.service.reader.model.MeasurementDto
import com.ijfs.toplchallenge.service.util.grouping.model.{DatedTransitTime, GroupedMeasurementRoads, Roads}
import com.ijfs.toplchallenge.service.util.time.TransitTimeResolverImplSpec.*
import com.ijfs.toplchallenge.service.util.time.model.MeasuredRoad
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

import scala.collection.immutable.{AbstractSet, SortedSet}

class TransitTimeResolverImplSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  val transitTimeResolver = new TransitTimeResolverImpl

  Feature("resolve") {

    Scenario("On empty grouped measurement roads and a request with average times, an empty set of measured roads should be returned") {
      Given("empty grouped measurement roads and a request with average times")
      val groupedMeasurementRoads: GroupedMeasurementRoads = Map[Roads, Set[DatedTransitTime]]()
      val bestPathRequest: ShortestPathRequest = ShortestPathRequestWithAverageTimes(
        initialIntersection = INTERSECTION_01,
        finalIntersection = INTERSECTION_02,
        filePath = FILE_PATH,
      )

      When("resolver is called")
      val result = transitTimeResolver.resolve(groupedMeasurementRoads, bestPathRequest)

      Then("an empty set of measured roads should be returned")
      result must be(empty)
    }

    Scenario("On empty grouped measurement roads and a request with weighted averages times, an empty set of measured roads should be returned") {
      Given("empty grouped measurement roads and a request with weighted averages times")
      val groupedMeasurementRoads: GroupedMeasurementRoads = Map[Roads, Set[DatedTransitTime]]()
      val bestPathRequest: ShortestPathRequest = ShortestPathRequestWithWeightedTimes(
        initialIntersection = INTERSECTION_01,
        finalIntersection = INTERSECTION_02,
        filePath = FILE_PATH,
      )

      When("resolver is called")
      val result = transitTimeResolver.resolve(groupedMeasurementRoads, bestPathRequest)

      Then("an empty set of measured roads should be returned")
      result must be(empty)
    }

    Scenario("On one grouped measurement road with one measurement and a request with average times, one measured road with the same transit time should be returned") {
      Given("one grouped measurement road with one measurement and a request with average times")
      val groupedMeasurementRoads: GroupedMeasurementRoads = Map[Roads, Set[DatedTransitTime]](
        Roads(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        ) -> Set(
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_A,
            transitTime = TRANSIT_TIME_01,
          )
        )
      )
      val bestPathRequest: ShortestPathRequest = ShortestPathRequestWithAverageTimes(
        initialIntersection = INTERSECTION_01,
        finalIntersection = INTERSECTION_02,
        filePath = FILE_PATH,
      )

      When("resolver is called")
      val result = transitTimeResolver.resolve(groupedMeasurementRoads, bestPathRequest)

      Then("an empty set of measured roads should be returned")
      result must (have size 1 and contain(
        MeasuredRoad(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          averageTransitTime = TRANSIT_TIME_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        )
      ))
    }

    Scenario("On one grouped measurement road with one measurement and a request with weighted average times, one measured road with the same transit time should be returned") {
      Given("one grouped measurement road with one measurement and a request with weighted average times")
      val groupedMeasurementRoads: GroupedMeasurementRoads = Map[Roads, Set[DatedTransitTime]](
        Roads(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        ) -> Set(
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_A,
            transitTime = TRANSIT_TIME_01,
          )
        )
      )
      val bestPathRequest: ShortestPathRequest = ShortestPathRequestWithWeightedTimes(
        initialIntersection = INTERSECTION_01,
        finalIntersection = INTERSECTION_02,
        filePath = FILE_PATH,
      )

      When("resolver is called")
      val result = transitTimeResolver.resolve(groupedMeasurementRoads, bestPathRequest)

      Then("an empty set of measured roads should be returned")
      result must (have size 1 and contain(
        MeasuredRoad(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          averageTransitTime = TRANSIT_TIME_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        )
      ))
    }

    Scenario("On two grouped measurement road with two measurements each and a request with average times, two measured road with their respectively average transit time should be returned") {
      Given("two grouped measurement road with two measurements each and a request with average times")
      val groupedMeasurementRoads: GroupedMeasurementRoads = Map[Roads, Set[DatedTransitTime]](
        Roads(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        ) -> Set(
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_A,
            transitTime = TRANSIT_TIME_01,
          ),
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_B,
            transitTime = TRANSIT_TIME_02,
          )
        ),
        Roads(
          startAvenue = START_AVENUE_02,
          startStreet = START_STREET_02,
          endAvenue = END_AVENUE_02,
          endStreet = END_STREET_02,
        ) -> Set(
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_A,
            transitTime = TRANSIT_TIME_03,
          ),
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_B,
            transitTime = TRANSIT_TIME_04,
          )
        ),
      )
      val bestPathRequest: ShortestPathRequest = ShortestPathRequestWithAverageTimes(
        initialIntersection = INTERSECTION_01,
        finalIntersection = INTERSECTION_02,
        filePath = FILE_PATH,
      )

      When("resolver is called")
      val result = transitTimeResolver.resolve(groupedMeasurementRoads, bestPathRequest)

      Then("two measured road with their respectively average transit time should be returned")
      result must (have size 2 and equal(
        Set(
          MeasuredRoad(
            startAvenue = START_AVENUE_01,
            startStreet = START_STREET_01,
            averageTransitTime = AVERAGE_TRANSIT_TIME_01_02,
            endAvenue = END_AVENUE_01,
            endStreet = END_STREET_01,
          ),
          MeasuredRoad(
            startAvenue = START_AVENUE_02,
            startStreet = START_STREET_02,
            averageTransitTime = AVERAGE_TRANSIT_TIME_03_04,
            endAvenue = END_AVENUE_02,
            endStreet = END_STREET_02,
          ),
        )
      ))
    }

    Scenario("On two grouped measurement road with two measurements each and a request with weighted average times, two measured road with their respectively weighted average transit time should be returned") {
      Given("two grouped measurement road with two measurements each and a request with weighted average times")
      val groupedMeasurementRoads: GroupedMeasurementRoads = Map[Roads, Set[DatedTransitTime]](
        Roads(
          startAvenue = START_AVENUE_01,
          startStreet = START_STREET_01,
          endAvenue = END_AVENUE_01,
          endStreet = END_STREET_01,
        ) -> Set(
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_A,
            transitTime = TRANSIT_TIME_01,
          ),
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_B,
            transitTime = TRANSIT_TIME_02,
          )
        ),
        Roads(
          startAvenue = START_AVENUE_02,
          startStreet = START_STREET_02,
          endAvenue = END_AVENUE_02,
          endStreet = END_STREET_02,
        ) -> Set(
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_A,
            transitTime = TRANSIT_TIME_03,
          ),
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_B,
            transitTime = TRANSIT_TIME_04,
          )
        ),
      )
      val bestPathRequest: ShortestPathRequest = ShortestPathRequestWithWeightedTimes(
        initialIntersection = INTERSECTION_01,
        finalIntersection = INTERSECTION_02,
        filePath = FILE_PATH,
      )

      When("resolver is called")
      val result = transitTimeResolver.resolve(groupedMeasurementRoads, bestPathRequest)

      Then("two measured road with their respectively weighted average transit time should be returned")
      result must (have size 2 and equal(
        Set(
          MeasuredRoad(
            startAvenue = START_AVENUE_01,
            startStreet = START_STREET_01,
            averageTransitTime = WEIGHTED_AVERAGE_TRANSIT_TIME_01_02,
            endAvenue = END_AVENUE_01,
            endStreet = END_STREET_01,
          ),
          MeasuredRoad(
            startAvenue = START_AVENUE_02,
            startStreet = START_STREET_02,
            averageTransitTime = WEIGHTED_AVERAGE_TRANSIT_TIME_03_04,
            endAvenue = END_AVENUE_02,
            endStreet = END_STREET_02,
          ),
        )
      ))
    }

  }

}

object TransitTimeResolverImplSpec {

  private final val FILE_PATH = "FILE_PATH"

  private final val AVENUE_01 = "AVENUE_01"
  private final val STREET_01 = "STREET_01"
  private final val AVENUE_02 = "AVENUE_02"
  private final val STREET_02 = "STREET_02"

  private final val INTERSECTION_01 = Intersection(
    avenue = AVENUE_01,
    street = STREET_01,
  )

  private final val INTERSECTION_02 = Intersection(
    avenue = AVENUE_02,
    street = STREET_02,
  )

  private final val MEASUREMENT_TIME_A = 10
  private final val MEASUREMENT_TIME_B = 100

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
  private final val TRANSIT_TIME_04 = 20000d

  private final val AVERAGE_TRANSIT_TIME_01_02 = 110d
  private final val AVERAGE_TRANSIT_TIME_03_04 = 11000d

  private final val WEIGHTED_AVERAGE_TRANSIT_TIME_01_02 = 80d
  private final val WEIGHTED_AVERAGE_TRANSIT_TIME_03_04 = 8000d

}