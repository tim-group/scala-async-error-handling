package com.timgroup.asyncerrorhandling

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalautils.Accumulation._
import org.scalautils._

class ParsePersonSpec extends FunSpec with Matchers with ScalaFutures {

  case class Person(name: String, age: Int)

  describe("Parsing a person asynchronously") {
    def eventuallyInputs(inputName: String, inputAge: String) = Future { (inputName, inputAge) }

    describe("Using accumulating Or's for error handling") {
      //
      // See http://www.scalautils.org/user_guide/OrAndEvery
      //
      def parseName(input: String): String Or One[ErrorMessage] = {
        val trimmed = input.trim
        if (!trimmed.isEmpty) Good(trimmed) else Bad(One(s""""${input}" is not a valid name"""))
      }

      def parseAge(input: String): Int Or One[ErrorMessage] = {
        try {
          val age = input.trim.toInt
          if (age >= 0) Good(age) else Bad(One(s""""${age}" is not a valid age"""))
        }
        catch {
          case _: NumberFormatException => Bad(One(s""""${input}" is not a valid integer"""))
        }
      }

      def isRound(i: Int): Validation[ErrorMessage] =
        if (i % 10 == 0) Pass else Fail(i + " is not a round number")

      def isDivBy3(i: Int): Validation[ErrorMessage] =
        if (i % 3 == 0) Pass else Fail(i + " is not divisible by 3")

      def bugDivBy0(i: Int): Validation[ErrorMessage] =
        if (42 / i != 42) Pass else Fail(i + " throws uncaught exception if i == 0")

      describe("Processing in serial in a single Future") {
        import OrConverters.OrFutureConverter

        def parsePerson(inputName: String, inputAge: String): Person Or Every[ErrorMessage] = {
          val name = parseName(inputName)
          val age = parseAge(inputAge) when(isRound, isDivBy3, bugDivBy0)
          withGood(name, age) { Person(_, _) }
        }

        def eventuallyParsePerson(inputName: String, inputAge: String) = for {
          (inputName, inputAge) <- eventuallyInputs(inputName, inputAge)
          person                <- parsePerson(inputName, inputAge).toSemanticErrorFuture
        } yield person

        it("accumulates multiple semantic errors") {
          val f = eventuallyParsePerson(" ", "31")
          f.failed.futureValue should be (SemanticError(Many(
            "\" \" is not a valid name",
            "31 is not a round number",
            "31 is not divisible by 3")))
        }

        it("returns a single uncaught exception") {
          val f = eventuallyParsePerson("Alice", "0")
          f.failed.futureValue shouldBe an [ArithmeticException]
        }

      }

      describe("Processing in serial across multiple Futures") {
        // TODO: Add tests
      }

      describe("Processing in parallel") {
        // TODO: Add tests
      }

    }

  }

}