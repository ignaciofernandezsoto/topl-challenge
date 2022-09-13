package com.ijfs.toplchallenge.service.error

object Failures {

  case object InvalidRequestArguments extends ToplException("Please start the program with the following parameters format (starting intersection, ending intersection, file path, transit time average strategy [optional, defaults to AVERAGE]): avenue,street avenue,street sample-data.json AVERAGE|WEIGHTED_AVERAGE")
  case class InvalidRequestIntersection(invalidIntersection: String) extends ToplException(s"Invalid intersection. Correct format is AVENUE,STREET. For instance: C,2. Invalid intersection=[$invalidIntersection]")

  case class InvalidRequestAvenue(invalidAvenue: String) extends ToplException(s"Invalid Avenue. It should have only a letter. Example: C. Invalid Avenue=[$invalidAvenue]")

  case class InvalidRequestStreet(invalidStreet: String) extends ToplException(s"Invalid Street. It should have a whole number. Example: 1337. Invalid Street=[$invalidStreet]")
  case class ParseException(e: Throwable) extends ToplException("Couldn't parse sample data", Some(e))
  case class NonExistentNode[T](node: T) extends ToplException(s"Node doesn't exist in Graph, node=[$node]")

}

abstract class ToplException(
                              val message: String,
                              val error: Option[Throwable] = None
                            )