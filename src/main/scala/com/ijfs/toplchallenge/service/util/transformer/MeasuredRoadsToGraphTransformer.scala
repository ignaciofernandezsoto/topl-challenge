package com.ijfs.toplchallenge.service.util.transformer

import com.ijfs.toplchallenge.service.model.Intersection
import com.ijfs.toplchallenge.service.util.dijkstra.model.Graph
import com.ijfs.toplchallenge.service.util.time.model.MeasuredRoad

trait MeasuredRoadsToGraphTransformer:
  def transform(measuredRoads: Set[MeasuredRoad]): Graph[Intersection]

object MeasuredRoadsToGraphTransformer:
  def apply: MeasuredRoadsToGraphTransformer = new MeasuredRoadsToGraphTransformerImpl
