package com.ijfs.toplchallenge.service.util.time

import com.ijfs.toplchallenge.service.model.ShortestPathRequest
import com.ijfs.toplchallenge.service.util.grouping.model.GroupedMeasurementRoads
import com.ijfs.toplchallenge.service.util.time.model.MeasuredRoad

trait TransitTimeResolver:
  def resolve(
               groupedMeasurementRoads: GroupedMeasurementRoads,
               request: ShortestPathRequest,
             ): Set[MeasuredRoad]

object TransitTimeResolver:
  def apply: TransitTimeResolver = new TransitTimeResolverImpl