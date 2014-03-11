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

class FunctionConvertersSpec extends FunSpec with Matchers with ScalaFutures {
  val future = Future.successful(-1)

  describe("Function1 converters") {
    import com.timgroup.asyncerrorhandling.FunctionConverters._

    describe("Or to Future") {
      val plusGood = (x: Int) => {
        if (x >= 0) Good(x) else Bad("negative")
      }
	  it("handles Good") {
	    val goodF = Future { 1 }.flatMap(plusGood)

        goodF.futureValue should be (1)
      }

	  it("handles Bad") {
	    val badF = Future { -1 }.flatMap(plusGood)

	    badF.failed.futureValue should be (SemanticError("negative"))
	  }
    }

    describe("Either to Future") {
      val plusRight = (x: Int) => {
        if (x >= 0) Right(x) else Left("negative")
      }
	  it("handles Right") {
	    val rightF = Future { 1 }.flatMap(plusRight)

        rightF.futureValue should be (1)
      }

	  it("handles Left") {
	    val leftF = Future { -1 }.flatMap(plusRight)

	    leftF.failed.futureValue should be (SemanticError("negative"))
	  }
    }

    describe("Try to Future") {
      val e = new RuntimeException("negative")
      val plusSuccess = (x: Int) => {
        if (x >= 0) Success(x) else Failure(e)
      }

      it("handles Success") {
        val successF = Future { 1 }.flatMap(plusSuccess)

        successF.futureValue should be (1)
      }

      it("handles Failure") {
        val failureF = Future { -1 }.flatMap(plusSuccess)

        failureF.failed.futureValue should be (e)
      }
    }
  }
}
