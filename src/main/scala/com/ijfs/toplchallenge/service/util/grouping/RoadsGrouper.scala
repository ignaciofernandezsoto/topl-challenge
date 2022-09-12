package com.ijfs.toplchallenge.service.util.grouping

import com.ijfs.toplchallenge.service.reader.model.TrafficFlowDto
import com.ijfs.toplchallenge.service.util.grouping.model.GroupedMeasurementRoads

trait RoadsGrouper:
  def group(trafficFlow: TrafficFlowDto): GroupedMeasurementRoads

object RoadsGrouper:
  def apply: RoadsGrouper = new RoadsGrouperImpl
