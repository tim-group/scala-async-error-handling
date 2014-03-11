package com.timgroup.asyncerrorhandling

import scala.util.Try
import scala.util.Failure
import scala.util.Success

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures

class TryConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  val ex = new RuntimeException("error")
  val failure: Try[Int] = Failure(ex)
  val success: Try[Int] = Success(42)

  describe("Try to Future conversions") {
    it("converts a Failure with an exception to a Future which is failed with the exception") {
      val f = TryConverters.toFuture(failure)
      f.failed.futureValue should be (ex)
    }

    it("converts a Succuess to a Future which is successful with the value") {
      val f = TryConverters.toFuture(success)
      f.futureValue should be (42)
    }

    it("provides an implicit to enrich a Try with a conversion method to Future") {
      import TryConverters.TryFutureConverter
      val f = success.toFuture
      f.futureValue should be (42)
    }
  }

}
