package com.timgroup.asyncerrorhandling

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalautils.ErrorMessage

class EitherConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  val right: Either[ErrorMessage, Int] = Right(42)
  val left:  Either[ErrorMessage, Int] = Left("error")

  describe("Either to Future conversions") {
    it("converts a Left to a Future which is failed with a SemanticError") {
      val f = EitherConverters.toFuture(left)
      f.failed.futureValue should be (SemanticError("error"))
    }

    it("converts a Right to a Future which is successful with the value") {
      val f = EitherConverters.toFuture(right)
      f.futureValue should be (42)
    }

    it("provides an implicit to enrich an Either with a conversion method to Future") {
      import EitherConverters.EitherFutureConverter
      val f = right.toSemanticErrorFuture
      f.futureValue should be (42)
    }
  }

}
