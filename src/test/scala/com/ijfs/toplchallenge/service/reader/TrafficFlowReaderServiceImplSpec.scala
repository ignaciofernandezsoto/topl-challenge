package com.ijfs.toplchallenge.service.reader

import cats.Applicative
import cats.effect.unsafe.implicits.global
import cats.implicits.*
import cats.syntax.all.*
import com.ijfs.toplchallenge.service.error.Failures.ParseException
import com.ijfs.toplchallenge.service.reader.TrafficFlowReaderServiceImplSpec.*
import com.ijfs.toplchallenge.service.reader.model.{MeasurementDto, TrafficFlowDto, TrafficMeasurementDto}
import io.circe.DecodingFailure
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

import java.io.FileNotFoundException

class TrafficFlowReaderServiceImplSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  type IO[+A] = cats.effect.IO[A]

  val readerService: TrafficFlowReaderService[IO] = new TrafficFlowReaderServiceImpl[IO]

  Feature("read") {

    Scenario("on a correct read, the traffic flow should be returned") {
      Given("a correct read")
      val fileName = CORRECT_FILE_NAME

      When("reader is called")
      val result = readerService.read(fileName)

      Then("the traffic flow should be returned")
      result must be(
        Right(
          TrafficFlowDto(
            trafficMeasurements = Set(
              TrafficMeasurementDto(
                measurementTime = 86544,
                measurements = Set(
                  MeasurementDto(
                    startAvenue = "A",
                    startStreet = "1",
                    transitTime = 28.000987663134676d,
                    endAvenue = "B",
                    endStreet = "1",
                  ),
                  MeasurementDto(
                    startAvenue = "A",
                    startStreet = "2",
                    transitTime = 59.71131185379898d,
                    endAvenue = "A",
                    endStreet = "1",
                  ),
                )
              )
            )
          )
        ).pure[IO]
      )
    }

    Scenario("on an incorrect read, a parse failure with a decoding failure should be returned") {
      Given("an incorrect read")
      val fileName = DECODING_ERROR_FILE_NAME

      When("reader is called")
      val result = readerService.read(fileName)

      Then("a parse failure with a decoding failure should be returned")
      result.unsafeToFuture() map {
        case Left(ParseException(_: DecodingFailure)) => succeed
        case e => fail(s"Not expected to receive $e")
      }
    }

  }

  Scenario("on a non existent file, a parse failure with a file not found exception should be returned") {
    Given("a non existent file")
    val fileName = NON_EXISTENT_ERROR_FILE_NAME

    When("reader is called")
    val result = readerService.read(fileName)

    Then("a parse failure with a file not found exception should be returned")
    result.unsafeToFuture() map {
      case Left(ParseException(_: FileNotFoundException)) => succeed
      case e => fail(s"Not expected to receive $e")
    }
  }

}

object TrafficFlowReaderServiceImplSpec {

  private final val CORRECT_FILE_NAME = "./src/test/resources/correct-sample-data.json"
  private final val DECODING_ERROR_FILE_NAME = "./src/test/resources/incorrect-sample-data.json"
  private final val NON_EXISTENT_ERROR_FILE_NAME = "./src/test/resources/non-existent-sample-data.json"

}
