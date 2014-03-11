package com.timgroup.asyncerrorhandling


import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalautils.Bad
import org.scalautils.Good
import org.scalautils.Or
import scala.util.Try
import scala.util.Failure
import scala.util.Success

class TryConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  describe("TryFuture converter") {
    it("should convert a Failure into a Future with exception") {
      val e = new RuntimeException("error")
      val bad: Try[Int] = Failure(e)

      val f = TryConverters.toFuture(bad)
      f.failed.futureValue should be (e)
    }

    it("should convert Succuess into a Future with the value") {
      val good: Try[Int] = Success(42)

      val f = TryConverters.toFuture(good)
      f.futureValue should be (42)
    }

    it("should be able to use the implicits") {
      val good: Try[Int] = Success(42)

      import TryConverters.TryFutureConverter
      val f = good.toFuture
      f.futureValue should be (42)
    }
  }
}
