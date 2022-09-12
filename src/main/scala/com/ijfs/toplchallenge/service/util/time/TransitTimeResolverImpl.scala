package com.ijfs.toplchallenge.service.util.time

import com.ijfs.toplchallenge.service.model.{ShortestPathRequest, ShortestPathRequestWithAverageTimes, ShortestPathRequestWithWeightedTimes}
import com.ijfs.toplchallenge.service.reader.model.MeasurementDto
import com.ijfs.toplchallenge.service.util.grouping.model.{DatedTransitTime, GroupedMeasurementRoads, Roads}
import com.ijfs.toplchallenge.service.util.time.model.MeasuredRoad

import scala.collection.immutable.{AbstractSet, SortedSet}

class TransitTimeResolverImpl
  extends TransitTimeResolver {

  override def resolve(
                        groupedMeasurementRoads: GroupedMeasurementRoads,
                        request: ShortestPathRequest,
                      ): Set[MeasuredRoad] =
    groupedMeasurementRoads.foldLeft(Set.empty[MeasuredRoad]) {
      case (
        measuredRoads,
        (Roads(startAvenue, startStreet, endAvenue, endStreet), datedTransitTimes)
        ) =>

        measuredRoads + MeasuredRoad(
          startAvenue = startAvenue,
          startStreet = startStreet,
          averageTransitTime = request match
            case _: ShortestPathRequestWithAverageTimes => doAverage(datedTransitTimes)
            case _: ShortestPathRequestWithWeightedTimes => doWeightedAverage(datedTransitTimes),
          endAvenue = endAvenue,
          endStreet = endStreet
        )
    }

  private def doAverage(datedTransitTimes: Set[DatedTransitTime]): Double = datedTransitTimes.map(_.transitTime).sum / datedTransitTimes.size

  // TODO could be improved to support dynamic weights
  private def doWeightedAverage(datedTransitTimes: Set[DatedTransitTime]): Double = {

    val weightTotal = 100d

    val measurementsPlusOne = datedTransitTimes.size + 1

    val measurementsPlusOneCountAverage = weightTotal / measurementsPlusOne.toDouble

    val measurements = datedTransitTimes.size

    val firstWeightedAverage = weightTotal - (measurements - 1) * measurementsPlusOneCountAverage

    datedTransitTimes.toList
      .sortBy(_.measurementTime) match
      case ::(DatedTransitTime(_, transitTime), Nil) => transitTime
      case ::(DatedTransitTime(measurementTime, transitTime), next) =>
        val weightedTransitTimes = DatedTransitTime(measurementTime, transitTime * firstWeightedAverage / 100) :: next map {
          case dtt @ DatedTransitTime(_, transitTime) =>
            dtt.copy(transitTime = transitTime * measurementsPlusOneCountAverage / 100)
        }
        doAverage(weightedTransitTimes.toSet)
      case Nil => 0d // TODO use refined
  }

}