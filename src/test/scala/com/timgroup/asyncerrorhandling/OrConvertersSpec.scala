package com.timgroup.asyncerrorhandling

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures

import org.scalautils.Bad
import org.scalautils.Good
import org.scalautils.Every
import org.scalautils.Many
import org.scalautils.Or

class OrConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  type ErrorMessage = String
  val good: Int Or ErrorMessage = Good(42)
  val bad:  Int Or ErrorMessage = Bad("error")
  val manyBad: Int Or Every[ErrorMessage] = Bad(Many("error1", "error2"))

  describe("Or to Future conversions") {
    it("converts a Bad to a Future which is failed with a SemanticError") {
      val f = OrConverters.toFuture(bad)
      f.failed.futureValue should be (SemanticError("error"))
    }

    it("converts a Bad containing multiple errors to a Future which is failed with a SemanticError") {
      val f = OrConverters.toFuture(manyBad)
      f.failed.futureValue should be (SemanticError(Many("error1", "error2")))
    }

    it("converts a Good to a Future which is successful with the value") {
      val f = OrConverters.toFuture(good)
      f.futureValue should be (42)
    }

    it("provides an implicit to enrich an Or with a conversion method to Future") {
      import OrConverters.OrFutureConverter
      val f = good.toSemanticErrorFuture
      f.futureValue should be (42)
    }
  }

  describe("Or to Try conversions") {
    it("converts a Bad to a Try which is a failure with a SemanticError") {
      val f = OrConverters.toTry(bad)
      f.failed.get should be (SemanticError("error"))
    }

    it("converts a Bad containing multiple errors to a Try which is a failure with a SemanticError") {
      val f = OrConverters.toTry(manyBad)
      f.failed.get should be (SemanticError(Many("error1", "error2")))
    }

    it("converts a Good to a Try which is a success with the value") {
      val f = OrConverters.toTry(good)
      f.get should be (42)
    }

    it("provides an implicit to enrich an Or with a conversion method to Try") {
      import OrConverters.OrTryConverter
      val f = good.toSemanticErrorTry
      f.get should be (42)
    }
  }

}
