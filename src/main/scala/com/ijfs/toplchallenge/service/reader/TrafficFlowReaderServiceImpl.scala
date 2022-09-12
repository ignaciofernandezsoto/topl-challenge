package com.ijfs.toplchallenge.service.reader

import cats.Applicative
import cats.implicits.*
import cats.syntax.all.*
import io.circe.*
import io.circe.Decoder.Result
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import com.ijfs.toplchallenge.service.error.Failures.ParseException
import com.ijfs.toplchallenge.service.error.ToplException
import com.ijfs.toplchallenge.service.reader.TrafficFlowReaderService
import com.ijfs.toplchallenge.service.reader.model.TrafficFlowDto

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

class TrafficFlowReaderServiceImpl[F[_] : Applicative]
  extends TrafficFlowReaderService[F] {

  override def read(fileName: String): F[Either[ToplException, TrafficFlowDto]] =
    Using(Source.fromFile(fileName))(_.getLines.mkString)
      .map(parse)
      .map(_.flatMap(_.as[TrafficFlowDto])) match {
      case Failure(e) => ParseException(e).asLeft.pure[F]
      case Success(Left(e)) => ParseException(e).asLeft.pure[F]
      case Success(Right(trafficFlow)) => trafficFlow.asRight.pure[F]
    }

}