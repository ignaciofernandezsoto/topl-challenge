package com.ijfs.toplchallenge.codec

import com.ijfs.toplchallenge.service.model.AverageStrategy
import io.circe.{Decoder, Encoder}
import io.circe._

import scala.compiletime.summonAll
import scala.deriving.Mirror


object AverageStrategyCodecs:
  inline def stringEnumDecoder[T](using m: Mirror.SumOf[T]): Decoder[T] =
    val elemInstances = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
    val elemNames = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(_.value)
    val mapping = (elemNames zip elemInstances).toMap
    Decoder[String].emap { name =>
      mapping.get(name).fold(Left(s"Name $name is invalid value"))(Right(_))
    }

  inline def stringEnumEncoder[T](using m: Mirror.SumOf[T]): Encoder[T] =
    val elemInstances = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
    val elemNames = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(_.value)
    val mapping = (elemInstances zip elemNames).toMap
    Encoder[String].contramap[T](mapping.apply)

  given decoder: Decoder[AverageStrategy] = stringEnumDecoder[AverageStrategy]
  given encoder: Encoder[AverageStrategy] = stringEnumEncoder[AverageStrategy]