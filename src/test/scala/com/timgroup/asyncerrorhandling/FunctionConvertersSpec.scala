package com.timgroup.asyncerrorhandling

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalautils.Bad
import org.scalautils.Good

class FunctionConvertersSpec extends FunSpec with Matchers with ScalaFutures {

  describe("Function1 converters") {
    import com.timgroup.asyncerrorhandling.FunctionConverters._
    val posF = Future {  1 }
    val negF = Future { -1 }

    describe("Or to Future") {
      val nonNegativeGood = (x: Int) => if (x >= 0) Good(x) else Bad("negative")

      it("handles Good") {
        val f = posF.flatMap(nonNegativeGood)
        f.futureValue should be (1)
      }

      it("handles Bad") {
        val f = negF.flatMap(nonNegativeGood)
        f.failed.futureValue should be (SemanticError("negative"))
      }
    }

    describe("Either to Future") {
      val nonNegativeRight = (x: Int) => if (x >= 0) Right(x) else Left("negative")

      it("handles Right") {
        val f = posF.flatMap(nonNegativeRight)
        f.futureValue should be (1)
      }

      it("handles Left") {
        val f = negF.flatMap(nonNegativeRight)
        f.failed.futureValue should be (SemanticError("negative"))
      }
    }

    describe("Try to Future") {
      val ex = new RuntimeException("negative")
      val nonNegativeSuccess = (x: Int) => if (x >= 0) Success(x) else Failure(ex)

      it("handles Success") {
        val f = posF.flatMap(nonNegativeSuccess)
        f.futureValue should be (1)
      }

      it("handles Failure") {
        val f = negF.flatMap(nonNegativeSuccess)
        f.failed.futureValue should be (ex)
      }
    }

  }

}
