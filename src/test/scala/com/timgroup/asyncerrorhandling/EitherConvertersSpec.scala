package com.timgroup.asyncerrorhandling

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures

import org.scalautils.Bad
import org.scalautils.Good
import org.scalautils.Or

class EitherConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  describe("EitherFuture converter") {
    it("should convert a Left into a Future with a SemanticError") {
      val bad: Either[String, Int] = Left("error")

      val f = EitherConverters.toFuture(bad)
      f.failed.futureValue should be (SemanticError("error"))
    }

    it("should convert Right into a Future with the value") {
      val good: Either[String, Int] = Right(42)

      val f = EitherConverters.toFuture(good)
      f.futureValue should be (42)
    }

    it("should be able to use the implicits") {
      val good: Either[String, Int] = Right(42)

      import EitherConverters.EitherFutureConverter
      val f = good.toSemanticErrorFuture
      f.futureValue should be (42)
    }
  }
}
