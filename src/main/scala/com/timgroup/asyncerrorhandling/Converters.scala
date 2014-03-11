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
import scala.concurrent.ExecutionContext

case class SemanticError[T](value: T) extends RuntimeException() with NoStackTrace

trait FutureConverters {
  implicit class FutureFlatMapConverters[A](future: Future[A]) {
    import OrConverters.OrFutureConverter
    import TryConverters.TryFutureConverter
    import EitherConverters.EitherFutureConverter

    def flatMapOr[B, C](orF: A => B Or C)(implicit ec: ExecutionContext): Future[B] = {
      future.flatMap(orF(_).toSemanticErrorFuture)
    }

    def flatMapTry[B](tryF: A => Try[B])(implicit ec: ExecutionContext): Future[B] = {
      future.flatMap(tryF(_).toFuture)
    }

    def flatMapEither[C, B](eitherF: A => Either[C, B])(implicit ec: ExecutionContext): Future[B] = {
      future.flatMap(eitherF(_).toSemanticErrorFuture)
    }
  }
}
object FutureConverters extends FutureConverters

trait TryConverters {
    def toFuture[A](t: Try[A]): Future[A] = {
      t match {
        case Success(good) => Future.successful(good)
        case Failure(bad)   => Future.failed(bad)
      }
    }

    implicit class TryFutureConverter[A](v: Try[A]) {
      def toFuture: Future[A] = Converters.toFuture(v)
    }
}
object TryConverters extends TryConverters

trait EitherConverters {
  def toFuture[L, R](either: Either[L, R]): Future[R] = {
    either match {
      case Right(good) => Future.successful(good)
      case Left(bad)   => Future.failed(SemanticError(bad))
    }
  }

  implicit class EitherFutureConverter[L, R](either: Either[L, R]) {
    def toSemanticErrorFuture: Future[R] = EitherConverters.toFuture(either)
  }
}
object EitherConverters extends EitherConverters

trait OrConverters {
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
object OrConverters extends OrConverters

object Converters extends OrConverters with TryConverters with FutureConverters