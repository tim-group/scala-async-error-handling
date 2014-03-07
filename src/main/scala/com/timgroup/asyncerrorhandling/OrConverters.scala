package com.timgroup.asyncerrorhandling

import scala.util.control.NoStackTrace
import org.scalautils.Or
import org.scalautils.Every
import scala.concurrent.Future
import org.scalautils.Good
import org.scalautils.Bad
import scala.util.Try
import scala.util.Success
import scala.util.Failure

case class SemanticError[T](value: T) extends RuntimeException() with NoStackTrace

object OrConverters {
    def toFuture[A, B](goodOrBad: A Or B): Future[A] = {
      goodOrBad match {
        case Good(good) => Future.successful(good)
        case Bad(bad)   => Future.failed(SemanticError(bad))
      }
    }

    def toTry[A, B](goodOrBad: A Or B): Try[A] = {
      goodOrBad match {
        case Good(good) => Success(good)
        case Bad(bad)   => Failure(SemanticError(bad))
      }
    }

    implicit class OrFutureConverter[A, B](v: A Or B) {
      def toSemanticErrorFuture: Future[A] = OrConverters.toFuture(v)
    }

    implicit class OrTryConverter[A, B](v: A Or B) {
      def toSemanticErrorTry: Try[A] = OrConverters.toTry(v)
    }
}
