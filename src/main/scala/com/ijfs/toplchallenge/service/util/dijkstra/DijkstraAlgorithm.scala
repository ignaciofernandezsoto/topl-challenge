package com.ijfs.toplchallenge.service.util.dijkstra

import com.ijfs.toplchallenge.service.error.ToplException
import com.ijfs.toplchallenge.service.util.dijkstra.model.{Graph, Path}

trait DijkstraAlgorithm:
  def shortestPath[Node](
                          graph: Graph[Node],
                          source: Node,
                          end: Node,
                        ): Either[ToplException, List[Path[Node]]]

object DijkstraAlgorithm:
  def apply: DijkstraAlgorithm = new DijkstraAlgorithmImpl
