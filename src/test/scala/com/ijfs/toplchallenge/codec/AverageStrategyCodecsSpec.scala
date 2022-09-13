package com.ijfs.toplchallenge.codec

import io.circe._
import io.circe.parser.decode
import io.circe.syntax._
import com.ijfs.toplchallenge.service.model.AverageStrategy
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

class AverageStrategyCodecsSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  val codecs: AverageStrategyCodecs.type = AverageStrategyCodecs

  Feature("encoder") {

    Scenario("on an average enum value, the stringified average enum value should be returned") {

      Given("an average enum value")
      val enumValue = AverageStrategy.AVERAGE

      When("encoder is called")
      val result = enumValue.asJson(codecs.encoder)

      Then("the stringified average enum value should be returned")
      result.toString must be(s"\"${AverageStrategy.AVERAGE.toString}\"")

    }

    Scenario("on a weighted average enum value, the stringified weighted average enum value should be returned") {

      Given("a weighted average stringified enum value")
      val enumValue = AverageStrategy.WEIGHTED_AVERAGE

      When("encoder is called")
      val result = enumValue.asJson(codecs.encoder)

      Then("the stringified weighted average enum value should be returned")
      result.toString must be(s"\"${AverageStrategy.WEIGHTED_AVERAGE.toString}\"")

    }

  }

  Feature("decoder") {

    Scenario("on an average stringified enum value, the average enum value should be returned") {

      Given("an average stringified enum value")
      val stringifiedEnumValue = s"\"${AverageStrategy.AVERAGE.toString}\""

      When("decoder is called")
      val result = decode[AverageStrategy](stringifiedEnumValue)(codecs.decoder)

      Then("the average enum value should be returned")
      result must be(Right(AverageStrategy.AVERAGE))

    }

    Scenario("on a weighted average stringified enum value, the weighted average enum value should be returned") {

      Given("a weighted average stringified enum value")
      val stringifiedEnumValue = s"\"${AverageStrategy.WEIGHTED_AVERAGE.toString}\""

      When("decoder is called")
      val result = decode[AverageStrategy](stringifiedEnumValue)(codecs.decoder)

      Then("the weighted average enum value should be returned")
      result must be(Right(AverageStrategy.WEIGHTED_AVERAGE))

    }

  }

}
