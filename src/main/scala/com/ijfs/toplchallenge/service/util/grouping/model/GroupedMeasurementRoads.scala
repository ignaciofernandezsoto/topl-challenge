package com.ijfs.toplchallenge.service.util.grouping.model

type GroupedMeasurementRoads = Map[Roads, Set[DatedTransitTime]]

case class Roads(
                  startAvenue: String,
                  startStreet: String,
                  endAvenue: String,
                  endStreet: String,
                )

case class DatedTransitTime(measurementTime: Int, transitTime: Double)

private[grouping] case class DatedMeasurement(
                                               startAvenue: String,
                                               startStreet: String,
                                               datedTransitTime: DatedTransitTime,
                                               endAvenue: String,
                                               endStreet: String,
                                             )