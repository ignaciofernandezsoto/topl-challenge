package com.ijfs.toplchallenge.service.util.grouping

import com.ijfs.toplchallenge.service.reader.model.{MeasurementDto, TrafficFlowDto, TrafficMeasurementDto}
import com.ijfs.toplchallenge.service.util.grouping.model.{DatedMeasurement, DatedTransitTime, GroupedMeasurementRoads, Roads}

import scala.collection.immutable.{AbstractSet, SortedSet}

class RoadsGrouperImpl
  extends RoadsGrouper {

  override def group(
                      trafficFlow: TrafficFlowDto,
                    ): GroupedMeasurementRoads =
    trafficFlow.trafficMeasurements
      .flatMap {
        case TrafficMeasurementDto(measurementTime, measurements) =>
          measurements.map {
            case MeasurementDto(startAvenue, startStreet, transitTime, endAvenue, endStreet) =>
              DatedMeasurement(
                startAvenue = startAvenue,
                startStreet = startStreet,
                datedTransitTime = DatedTransitTime(
                  measurementTime,
                  transitTime,
                ),
                endAvenue = endAvenue,
                endStreet = endStreet,
              )
          }
      }.groupBy {
      case DatedMeasurement(startAvenue, startStreet, _, endAvenue, endStreet) =>
        Roads(
          startAvenue = startAvenue,
          startStreet = startStreet,
          endAvenue = endAvenue,
          endStreet = endStreet,
        )
    }.map {
      case (roads, datedMeasurements) => (
        roads,
        datedMeasurements.map(_.datedTransitTime)
      )
    }


}
