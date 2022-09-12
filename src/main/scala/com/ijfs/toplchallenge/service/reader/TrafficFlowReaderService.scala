package com.ijfs.toplchallenge.service.reader

import cats.Applicative
import com.ijfs.toplchallenge.service.error.ToplException
import com.ijfs.toplchallenge.service.reader.model.TrafficFlowDto

import scala.util.Try

trait TrafficFlowReaderService[F[_]: Applicative]:
  def read(fileName: String): F[Either[ToplException, TrafficFlowDto]]

object TrafficFlowReaderService:
  def apply[F[_]: Applicative]: TrafficFlowReaderService[F] = new TrafficFlowReaderServiceImpl[F]