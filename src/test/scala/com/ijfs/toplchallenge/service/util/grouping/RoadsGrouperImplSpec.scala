package com.ijfs.toplchallenge.service.util.grouping

import com.ijfs.toplchallenge.service.reader.model.{MeasurementDto, TrafficFlowDto, TrafficMeasurementDto}
import com.ijfs.toplchallenge.service.util.grouping.RoadsGrouperImplSpec.*
import com.ijfs.toplchallenge.service.util.grouping.model.{DatedTransitTime, GroupedMeasurementRoads, Roads}
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

import scala.collection.immutable.{AbstractSet, SortedSet}

class RoadsGrouperImplSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  val roadsGrouper = new RoadsGrouperImpl

  Feature("group") {

    Scenario("On empty traffic measurements, an empty map is returned") {
      Given("empty traffic measurements")
      val trafficFlow = TrafficFlowDto(
        trafficMeasurements = Set()
      )

      When("grouper is called")
      val result = roadsGrouper.group(trafficFlow)

      Then("an empty map is returned")
      result must be(empty)
    }

    Scenario("On a single traffic measurements with a single measurement, a map of that measurement is returned") {
      Given("a single traffic measurements with a single measurement")
      val trafficFlow = TrafficFlowDto(
        trafficMeasurements = Set(
          TrafficMeasurementDto(
            measurementTime = MEASUREMENT_TIME_A,
            measurements = Set(
              MeasurementDto(
                startAvenue = START_AVENUE_01,
                startStreet = START_STREET_01,
                transitTime = TRANSIT_TIME_01,
                endAvenue = END_AVENUE_01,
                endStreet = END_STREET_01,
              ),
            )
          )
        )
      )

      When("grouper is called")
      val result = roadsGrouper.group(trafficFlow)

      Then("a map of that measurement is returned")
      val expectedRoads = Roads(
        startAvenue = START_AVENUE_01,
        startStreet = START_STREET_01,
        endAvenue = END_AVENUE_01,
        endStreet = END_STREET_01,
      )
      result must {
        contain key expectedRoads and equal(Map(expectedRoads -> Set(
          DatedTransitTime(
            measurementTime = MEASUREMENT_TIME_A,
            transitTime = TRANSIT_TIME_01
          )
        )))
      }
    }

    Scenario("On two traffic measurements with a two measurements each, a map of two measurements with their respective data is returned") {
      Given("a single traffic measurements with a single measurement")
      val trafficFlow = TrafficFlowDto(
        trafficMeasurements = Set(
          TrafficMeasurementDto(
            measurementTime = MEASUREMENT_TIME_A,
            measurements = Set(
              MeasurementDto(
                startAvenue = START_AVENUE_01,
                startStreet = START_STREET_01,
                transitTime = TRANSIT_TIME_01,
                endAvenue = END_AVENUE_01,
                endStreet = END_STREET_01,
              ),
              MeasurementDto(
                startAvenue = START_AVENUE_02,
                startStreet = START_STREET_02,
                transitTime = TRANSIT_TIME_02,
                endAvenue = END_AVENUE_02,
                endStreet = END_STREET_02,
              ),
            )
          ),
          TrafficMeasurementDto(
            measurementTime = MEASUREMENT_TIME_B,
            measurements = Set(
              MeasurementDto(
                startAvenue = START_AVENUE_01,
                startStreet = START_STREET_01,
                transitTime = TRANSIT_TIME_03,
                endAvenue = END_AVENUE_01,
                endStreet = END_STREET_01,
              ),
              MeasurementDto(
                startAvenue = START_AVENUE_02,
                startStreet = START_STREET_02,
                transitTime = TRANSIT_TIME_04,
                endAvenue = END_AVENUE_02,
                endStreet = END_STREET_02,
              ),
            )
          ),
        )
      )

      When("grouper is called")
      val result = roadsGrouper.group(trafficFlow)

      Then("a map of that measurement is returned")
      val expectedRoads_01 = Roads(
        startAvenue = START_AVENUE_01,
        startStreet = START_STREET_01,
        endAvenue = END_AVENUE_01,
        endStreet = END_STREET_01,
      )
      val expectedRoads_02 = Roads(
        startAvenue = START_AVENUE_02,
        startStreet = START_STREET_02,
        endAvenue = END_AVENUE_02,
        endStreet = END_STREET_02,
      )
      result must {
        contain key expectedRoads_01 and contain key expectedRoads_02 and equal(
          Map(
            expectedRoads_01 -> Set(
              DatedTransitTime(
                measurementTime = MEASUREMENT_TIME_A,
                transitTime = TRANSIT_TIME_01
              ),
              DatedTransitTime(
                measurementTime = MEASUREMENT_TIME_B,
                transitTime = TRANSIT_TIME_03
              ),
            ),
            expectedRoads_02 -> Set(
              DatedTransitTime(
                measurementTime = MEASUREMENT_TIME_A,
                transitTime = TRANSIT_TIME_02
              ),
              DatedTransitTime(
                measurementTime = MEASUREMENT_TIME_B,
                transitTime = TRANSIT_TIME_04
              ),
            )
          )
        )
      }
    }

  }

}

object RoadsGrouperImplSpec {

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

  private final val TRANSIT_TIME_01 = 20
  private final val TRANSIT_TIME_02 = 200
  private final val TRANSIT_TIME_03 = 2000
  private final val TRANSIT_TIME_04 = 20000

}