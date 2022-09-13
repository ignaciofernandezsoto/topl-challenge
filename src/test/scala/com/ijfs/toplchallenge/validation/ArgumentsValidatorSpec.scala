package com.ijfs.toplchallenge.validation

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.ijfs.toplchallenge.service.error.Failures.{InvalidRequestArguments, InvalidRequestAvenue, InvalidRequestIntersection, InvalidRequestStreet}
import com.ijfs.toplchallenge.service.model.{Intersection, ShortestPathRequestWithAverageTimes, ShortestPathRequestWithWeightedTimes}
import com.ijfs.toplchallenge.validation.ArgumentsValidatorSpec.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.must.Matchers

class ArgumentsValidatorSpec
  extends AsyncFeatureSpec
    with GivenWhenThen
    with Matchers {

  val validator: ArgumentsValidator.type = ArgumentsValidator
  
  Feature("validateRequest") {
    
    Scenario("On no arguments, an InvalidRequestArguments invalid result should be returned") {
      
      Given("no arguments")
      val args = List.empty[String]

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestArguments invalid result should be returned")
      result must be(Invalid(NonEmptyList.one(InvalidRequestArguments)))
      
    }

    Scenario("On only one argument, an InvalidRequestArguments invalid result should be returned") {

      Given("only one argument")
      val args = RAW_INTERSECTION_01 :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestArguments invalid result should be returned")
      result must be(Invalid(NonEmptyList.one(InvalidRequestArguments)))

    }

    Scenario("On only two arguments, an InvalidRequestArguments invalid result should be returned") {

      Given("only two arguments")
      val args = RAW_INTERSECTION_01 :: RAW_INTERSECTION_02 :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestArguments invalid result should be returned")
      result must be(Invalid(NonEmptyList.one(InvalidRequestArguments)))

    }

    Scenario("On three arguments, with the first intersection being invalid, an InvalidRequestIntersection with the first argument should be returned") {

      Given("three arguments, with the first intersection being invalid")
      val args = WRONGLY_FORMATTED_INTERSECTION_01 :: RAW_INTERSECTION_02 :: FILE_PATH :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestIntersection with the first argument should be returned")
      result must be(Invalid(NonEmptyList.one(InvalidRequestIntersection(WRONGLY_FORMATTED_INTERSECTION_01))))

    }

    Scenario("On three arguments, with the two intersections being invalid, an InvalidRequestIntersection with both invalid intersections should be returned") {

      Given("three arguments, with the two intersections being invalid")
      val args = WRONGLY_FORMATTED_INTERSECTION_01 :: WRONGLY_FORMATTED_INTERSECTION_02 :: FILE_PATH :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestIntersection with both invalid intersections should be returned")
      result must be(Invalid(NonEmptyList.of(InvalidRequestIntersection(WRONGLY_FORMATTED_INTERSECTION_01), InvalidRequestIntersection(WRONGLY_FORMATTED_INTERSECTION_02))))

    }

    Scenario("On three arguments, with the first intersection having a wrongly formatted avenue, an InvalidRequestAvenue with the first argument's avenue should be returned") {

      Given("three arguments, with the first intersection having a wrongly formatted avenue")
      val args = WRONG_AVENUE_INTERSECTION_01 :: RAW_INTERSECTION_02 :: FILE_PATH :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestAvenue with the first argument's avenue should be returned")
      result must be(Invalid(NonEmptyList.one(InvalidRequestAvenue(WRONG_AVENUE_FORMAT))))

    }

    Scenario("On three arguments, with the first intersection having a wrongly formatted street, an InvalidRequestStreet with the first argument's street should be returned") {

      Given("three arguments, with the first intersection having a wrongly formatted street")
      val args = WRONG_STREET_INTERSECTION_01 :: RAW_INTERSECTION_02 :: FILE_PATH :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestStreet with the first argument's street should be returned")
      result must be(Invalid(NonEmptyList.one(InvalidRequestStreet(WRONG_STREET_FORMAT))))

    }

    Scenario("On four arguments, with the last argument being an invalid average strategy, an InvalidRequestArguments should be returned") {

      Given("four arguments, with the last argument being an invalid average strategy")
      val args = RAW_INTERSECTION_01 :: RAW_INTERSECTION_02 :: FILE_PATH :: WRONG_AVERAGE_STRATEGY :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("an InvalidRequestArguments should be returned")
      result must be(Invalid(NonEmptyList.one(InvalidRequestArguments)))

    }

    Scenario("On three valid intersection arguments, a request with average times strategy should be returned") {

      Given("three valid intersection arguments")
      val args = RAW_INTERSECTION_01 :: RAW_INTERSECTION_02 :: FILE_PATH :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("request with average times strategy should be returned")
      result must be(Valid(
        ShortestPathRequestWithAverageTimes(
          initialIntersection = INTERSECTION_01,
          finalIntersection = INTERSECTION_02,
          filePath = FILE_PATH,
        )
      ))

    }

    Scenario("On four valid intersection arguments with the average strategy being AVERAGE, a request with average times strategy should be returned") {

      Given("four valid intersection arguments with the average strategy being AVERAGE")
      val args = RAW_INTERSECTION_01 :: RAW_INTERSECTION_02 :: FILE_PATH :: AVERAGE_STRATEGY :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("a request with average times strategy should be returned")
      result must be(Valid(
        ShortestPathRequestWithAverageTimes(
          initialIntersection = INTERSECTION_01,
          finalIntersection = INTERSECTION_02,
          filePath = FILE_PATH,
        )
      ))

    }

    Scenario("On four valid intersection arguments with the average strategy being WEIGHTED_AVERAGE, a request with weighted average times strategy should be returned") {

      Given("four valid intersection arguments with the average strategy being WEIGHTED_AVERAGE")
      val args = RAW_INTERSECTION_01 :: RAW_INTERSECTION_02 :: FILE_PATH :: WEIGHTED_AVERAGE_STRATEGY :: Nil

      When("validator is called")
      val result = validator.validateRequest(args)

      Then("a request with weighted average times strategy should be returned")
      result must be(Valid(
        ShortestPathRequestWithWeightedTimes(
          initialIntersection = INTERSECTION_01,
          finalIntersection = INTERSECTION_02,
          filePath = FILE_PATH,
        )
      ))

    }
    
  }

}

object ArgumentsValidatorSpec {

  private final val AVENUE_A = "A"
  private final val AVENUE_B = "B"
  private final val STREET_1 = "1"
  private final val STREET_2 = "2"

  private final val RAW_INTERSECTION_01 = s"$AVENUE_A,$STREET_1"
  private final val RAW_INTERSECTION_02 = s"$AVENUE_B,$STREET_2"

  private final val INTERSECTION_01 = Intersection(
    avenue = AVENUE_A,
    street = STREET_1
  )
  private final val INTERSECTION_02 = Intersection(
    avenue = AVENUE_B,
    street = STREET_2
  )

  private final val AVERAGE_STRATEGY = "AVERAGE"
  private final val WEIGHTED_AVERAGE_STRATEGY = "WEIGHTED_AVERAGE"

  private final val WRONGLY_FORMATTED_INTERSECTION_01 = "C1"
  private final val WRONGLY_FORMATTED_INTERSECTION_02 = "2|A"

  private final val WRONG_AVENUE_FORMAT = "ASD"
  private final val WRONG_STREET_FORMAT = "B"

  private final val WRONG_AVENUE_INTERSECTION_01 = s"$WRONG_AVENUE_FORMAT,1"
  private final val WRONG_STREET_INTERSECTION_01 = s"$WRONG_STREET_FORMAT,B"

  private final val WRONG_AVERAGE_STRATEGY = "WRONG_AVERAGE_STRATEGY"

  private final val FILE_PATH = "FILE_PATH"

}