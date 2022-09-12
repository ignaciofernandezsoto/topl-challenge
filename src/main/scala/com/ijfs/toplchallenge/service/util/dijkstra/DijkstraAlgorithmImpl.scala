package com.ijfs.toplchallenge.service.util.dijkstra

import com.ijfs.toplchallenge.service.error.Failures.NonExistentNode
import com.ijfs.toplchallenge.service.error.ToplException
import com.ijfs.toplchallenge.service.util.dijkstra.model.{DistancesToTheSource, Graph, Path, PredecessorDistance, RelativeDistance, WeightedArc}
import io.github.arainko.ducktape.*

import scala.annotation.tailrec

// Algorithm based on the following:
// https://www.literateprograms.org/dijkstra_s_algorithm__scala_.html
// https://dev.to/jjb/part-17-finding-shortest-paths-in-graphs-using-dijkstra-s-bfs-554m
// https://www.youtube.com/watch?v=k1kLCB7AZbM
// https://www.youtube.com/watch?v=bZkzH5x0SKU
class DijkstraAlgorithmImpl extends DijkstraAlgorithm {

  def shortestPath[Node](
                          graph: Graph[Node],
                          source: Node,
                          end: Node,
                        ): Either[ToplException, List[Path[Node]]] = {
    if (source == end) return Right(Path(source, end, 0) :: Nil)

    for {
      _ <- graph.get(source).toRight(NonExistentNode(source))
      _ <- graph.get(end).toRight(NonExistentNode(end))
      shortestDistances <- Right(getShortestDistances(source, graph))
      shortestPath <- Right(reconstructShortestPath(source, end, shortestDistances))
    } yield shortestPath
  }

  private def reconstructShortestPath[Node](
                                             source: Node,
                                             end: Node,
                                             distancesToTheSource: DistancesToTheSource[Node]
                                           ): List[Path[Node]] =
    recursiveReconstructShortestPath(source, end, distancesToTheSource, List.empty[Path[Node]])

  @tailrec
  private def recursiveReconstructShortestPath[Node](
                                                      Source: Node,
                                                      end: Node,
                                                      distancesToTheSource: DistancesToTheSource[Node],
                                                      recPaths: List[Path[Node]]
                                                    ): List[Path[Node]] = distancesToTheSource.get(end) match
    case None | Some(PredecessorDistance(_, None)) => recPaths
    case Some(PredecessorDistance(relativeDistance, Source)) =>
      Path[Node](Source, end, relativeDistance.predecessorDistance) :: Nil
    case Some(PredecessorDistance(relativeDistance, Some(predecessor))) =>
      recursiveReconstructShortestPath[Node](Source, predecessor, distancesToTheSource,
        Path(predecessor, end, relativeDistance.predecessorDistance) :: recPaths
      )

  private def getShortestDistances[Node](source: Node, graph: Graph[Node]): DistancesToTheSource[Node] = {
    val nodes = graph.keySet

    val visitedNodes: Set[Node] = Set.empty
    val unvisitedNodes: Set[Node] = nodes
    val distancesToTheSource: DistancesToTheSource[Node] = nodes.map(node => {
      val shortestDistance = if (node == source) 0 else Double.PositiveInfinity
      (node, PredecessorDistance[Node](RelativeDistance(shortestDistance, shortestDistance)))
    }).to(Map)

    recursiveShortestDistances(source, graph, visitedNodes, unvisitedNodes, distancesToTheSource)
  }

  @tailrec
  private def recursiveShortestDistances[Node](
                                                node: Node,
                                                graph: Graph[Node],
                                                visitedNodes: Set[Node],
                                                unvisitedNodes: Set[Node],
                                                distancesToTheSource: DistancesToTheSource[Node]
                                              ): DistancesToTheSource[Node] = {
    val updatedDistancesToTheSource = graph(node)
      .toList
      .sortBy(_.weight)
      .foldLeft(distancesToTheSource) {
        case (distancesToTheSource, WeightedArc(neighbourNode, arcWeight)) =>

          // We need to reuse the already calculated shortest distance, if it has a predecessor
          val nodeDistanceToSource = distancesToTheSource(node) match
            case PredecessorDistance(distance, Some(_)) => distance.sourceDistance
            case PredecessorDistance(_, None) => 0d

          val arcDistanceToTheSource = arcWeight + nodeDistanceToSource

          if (distancesToTheSource(neighbourNode).distance.sourceDistance > arcDistanceToTheSource)
            distancesToTheSource.updated(
              neighbourNode,
              PredecessorDistance(RelativeDistance(arcWeight, arcDistanceToTheSource), Some(node))
            )
          else
            distancesToTheSource
      }

    val updatedVisitedNodes = visitedNodes + node

    val updatedUnvisitedNodes = unvisitedNodes - node

    updatedDistancesToTheSource
      .filter(distancesToTheSource => unvisitedNodes.contains(distancesToTheSource._1)).toList
      .sortBy(_._2.distance.sourceDistance)
      .headOption
      .map(_._1) match
      case Some(newNodeToExplore) => recursiveShortestDistances(newNodeToExplore, graph, updatedVisitedNodes, updatedUnvisitedNodes, updatedDistancesToTheSource)
      case None => updatedDistancesToTheSource
  }

}
