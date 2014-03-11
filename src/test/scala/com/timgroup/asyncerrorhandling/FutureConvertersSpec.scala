package com.timgroup.asyncerrorhandling


import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalautils.Bad
import org.scalautils.Good
import org.scalautils.Or
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success


class FutureConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  val future = Future.successful(-1)

  describe("Future converters") {
    import com.timgroup.asyncerrorhandling.FutureConverters.FutureFlatMapConverters
    describe("flatMapOr") {
	    it("maps a Bad to a SemanticError") {
	      val f = future.flatMapOr(_ => Bad("error"))
	      f.failed.futureValue should be (SemanticError("error"))
	    }

	    it("maps a Good to the value") {
	      val f = future.flatMapOr(_ => Good(42))
	      f.futureValue should be (42)
	    }
    }

    describe("flatMapEither") {
      it("maps a Left to a SemanticError") {
        val f = future.flatMapEither(_ => Left("error"))
        f.failed.futureValue should be (SemanticError("error"))
       }

      it("maps a Right to the value") {
        val f = future.flatMapEither(_ => Right(42))
        f.futureValue should be (42)
      }
    }

    describe("flatMapTry") {
      it("maps a Failure to an exception") {
        val e = new RuntimeException("error")
        val f = future.flatMapTry(_ => Failure(e))
        f.failed.futureValue should be (e)
      }

      it("maps a Success to the value") {
        val f = future.flatMapTry(_ => Success(42))
        f.futureValue should be (42)
      }
    }
  }
}
