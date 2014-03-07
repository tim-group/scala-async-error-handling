package com.timgroup.asyncerrorhandling


import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures

import org.scalautils.Bad
import org.scalautils.Good
import org.scalautils.Or

class OrConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  describe("Or Future converter") {
    it("should convert a Bad into a Future with a SemanticError") {
      val bad: Int Or String = Bad("error")

      val f = OrConverters.toFuture(bad)
      f.failed.futureValue should be (SemanticError("error"))
    }

    it("should convert Good into a Future with the value") {
      val good: Int Or String = Good(42)

      val f = OrConverters.toFuture(good)
      f.futureValue should be (42)
    }

    it("should be able to use the implicits") {
      val good: Int Or String = Good(42)

      import OrConverters.OrFutureConverter
      val f = good.toSemanticErrorFuture
      f.futureValue should be (42)
    }
  }

  describe("Or Try converter") {
    it("should convert a Bad into a Try with a SemanticError") {
      val bad: Int Or String = Bad("error")

      val f = OrConverters.toTry(bad)
      f.failed.get should be (SemanticError("error"))
    }

    it("should convert Good into a Try with the value") {
      val good: Int Or String = Good(42)

      val f = OrConverters.toTry(good)
      f.get should be (42)
    }

    it("should be able to use the implicits") {
      val good: Int Or String = Good(42)

      import OrConverters.OrTryConverter
      val f = good.toSemanticErrorTry
      f.get should be (42)
    }
  }
}
