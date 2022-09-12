package com.ijfs.toplchallenge.service.model

sealed trait ShortestPathRequest:
  def initialIntersection: Intersection
  def finalIntersection: Intersection
  def filePath: String

case class ShortestPathRequestWithAverageTimes(
                                                initialIntersection: Intersection,
                                                finalIntersection: Intersection,
                                                filePath: String,
                                              ) extends ShortestPathRequest

case class ShortestPathRequestWithWeightedTimes(
                                                 initialIntersection: Intersection,
                                                 finalIntersection: Intersection,
                                                 filePath: String,
                                               ) extends ShortestPathRequest

case class Intersection(
                         avenue: String,
                         street: String,
                       )