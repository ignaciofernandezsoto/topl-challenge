package com.ijfs.toplchallenge.service.error

object Failures {

  case object InvalidRequestArguments extends ToplException("Please start the program with the following parameters format (starting intersection, ending intersection, file path, transit time average strategy [optional, defaults to AVERAGE]): avenue,street avenue,street sample-data.json AVERAGE|WEIGHTED_AVERAGE")
  case class InvalidRequestIntersection(invalidIntersection: String) extends ToplException(s"Invalid intersection. Correct format is AVENUE,STREET. For instance: C,2. Invalid intersection=[$invalidIntersection]")
  case class ParseException(e: Throwable) extends ToplException("Couldn't parse sample data", Some(e))
  case class NonExistentNode[T](node: T) extends ToplException(s"Node doesn't exist in Graph, node=[$node]")

}

abstract class ToplException(
                              val message: String,
                              val error: Option[Throwable] = None
                            )