package com.ijfs.toplchallenge

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.std.Console
import cats.effect.kernel.Sync
import cats.syntax.all.*
import cats.effect.{ExitCode, IOApp}
import com.ijfs.toplchallenge.codec.AverageStrategyCodecs.{decoder, encoder}
import com.ijfs.toplchallenge.service.ShortestPathSolver
import com.ijfs.toplchallenge.service.model.{AverageStrategy, Intersection, ShortestPathRequest, ShortestPathRequestWithAverageTimes, ShortestPathRequestWithWeightedTimes}
import com.ijfs.toplchallenge.validation.ArgumentsValidator.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.{Decoder, Encoder}

object Main extends IOApp {
  type IO[+A] = cats.effect.IO[A]

  def run(args: List[String]): IO[ExitCode] = {
    val validatedRequest = validateRequest(args)

    validatedRequest match
      case Valid(request) =>
        ShortestPathSolver.apply[IO]
          .solve(request)
          .map {
            case Left(e) => System.err.println(e)
            case Right(shortestPath) =>
              println(shortestPath.asJson)
          }
          .as(ExitCode.Success)
      case Invalid(e) =>
        cats.effect.IO(System.err.println(s"Error while validating request with error=[${e.map(_.message)}]"))
          .as(ExitCode.Error)

  }

}
