package com.ijfs.toplchallenge.validation

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.ijfs.toplchallenge.service.model.{AverageStrategy, Intersection, ShortestPathRequest, ShortestPathRequestWithAverageTimes, ShortestPathRequestWithWeightedTimes}
import cats.syntax.all.*
import com.ijfs.toplchallenge.service.error.Failures.{InvalidRequestArguments, InvalidRequestAvenue, InvalidRequestIntersection, InvalidRequestStreet}

import scala.language.implicitConversions
import scala.util.matching.UnanchoredRegex

object ArgumentsValidator:
  def validateRequest(args: List[String]): ValidationResult[ShortestPathRequest] = {
    val maybeRawRequest = args match
      case firstElement :: secondElement :: thirdElement :: fourthElement :: _ if AverageStrategy.values.map(_.toString.toUpperCase).contains(fourthElement.toUpperCase) =>
        (firstElement, secondElement, thirdElement, AverageStrategy.valueOf(fourthElement.toUpperCase)).validNel
      case firstElement :: secondElement :: thirdElement :: Nil =>
        (firstElement, secondElement, thirdElement, AverageStrategy.AVERAGE).validNel
      case _ =>
        InvalidRequestArguments.invalidNel

    maybeRawRequest match {
      case Valid((possibleStartingIntersection, possibleEndingIntersection, possibleFilePath, averageStrategy)) =>
        (
          validateIntersection(possibleStartingIntersection),
          validateIntersection(possibleEndingIntersection),
          ).mapN({
          case (startingIntersection, endingIntersection) => averageStrategy match
            case AverageStrategy.AVERAGE => ShortestPathRequestWithAverageTimes(startingIntersection, endingIntersection, possibleFilePath)
            case AverageStrategy.WEIGHTED_AVERAGE => ShortestPathRequestWithWeightedTimes(startingIntersection, endingIntersection, possibleFilePath)
        })
      case Invalid(e) => Invalid(e)
    }
  }

  private def validateIntersection(input: String): ValidationResult[Intersection] =
    input.split(",").toList match
      case rawAvenue :: rawStreet :: Nil =>
        (validateAvenue(rawAvenue), validateStreet(rawStreet)).mapN({
          case (avenue, street) => Intersection(avenue, street)
        })

      case _ => InvalidRequestIntersection(input).invalidNel

  private def validateAvenue(avenue: String): ValidationResult[String] =
    if (avenue.matches("[a-zA-Z]")) avenue.validNel else InvalidRequestAvenue(avenue).invalidNel

  private def validateStreet(street: String): ValidationResult[String] =
    if (street.matches("[0-9]+")) street.validNel else InvalidRequestStreet(street).invalidNel