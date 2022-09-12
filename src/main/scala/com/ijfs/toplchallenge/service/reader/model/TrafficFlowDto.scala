package com.ijfs.toplchallenge.service.reader.model

import scala.collection.immutable.SortedSet

case class TrafficFlowDto(
                           trafficMeasurements: Set[TrafficMeasurementDto], // I assume there are no repeated traffic measurements. Order is given by its 'measurementTime' field
                         )

case class TrafficMeasurementDto(
                                  measurementTime: Int,
                                  measurements: Set[MeasurementDto], // I assume there are no repeated measurements and they are non-ordered
                                )

case class MeasurementDto(
                           startAvenue: String,
                           startStreet: String,
                           transitTime: Double,
                           endAvenue: String,
                           endStreet: String,
                         )