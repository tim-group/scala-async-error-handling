package com.timgroup.asyncerrorhandling

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.control.NoStackTrace
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import org.scalautils.Bad
import org.scalautils.Good
import org.scalautils.Or

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
  def toFuture[A](t: Try[A]): Future[A] = t match {
    case Success(good) => Future.successful(good)
    case Failure(bad)   => Future.failed(bad)
  }

  implicit class TryFutureConverter[A](t: Try[A]) {
    def toFuture: Future[A] = Converters.toFuture(t)
  }
}

object TryConverters extends TryConverters

trait EitherConverters {
  def toFuture[L, R](e: Either[L, R]): Future[R] = e match {
    case Right(good) => Future.successful(good)
    case Left(bad)   => Future.failed(SemanticError(bad))
  }

  implicit class EitherFutureConverter[L, R](e: Either[L, R]) {
    def toSemanticErrorFuture: Future[R] = EitherConverters.toFuture(e)
  }
}

object EitherConverters extends EitherConverters

trait OrConverters {
  def toFuture[A, B](goodOrBad: A Or B): Future[A] = goodOrBad match {
    case Good(good) => Future.successful(good)
    case Bad(bad)   => Future.failed(SemanticError(bad))
  }

  def toTry[A, B](goodOrBad: A Or B): Try[A] = goodOrBad match {
    case Good(good) => Success(good)
    case Bad(bad)   => Failure(SemanticError(bad))
  }

  implicit class OrFutureConverter[A, B](o: A Or B) {
    def toSemanticErrorFuture: Future[A] = OrConverters.toFuture(o)
  }

  implicit class OrTryConverter[A, B](o: A Or B) {
    def toSemanticErrorTry: Try[A] = OrConverters.toTry(o)
  }
}

object OrConverters extends OrConverters

trait FunctionConverters {
  implicit def orFunctionToSemanticErrorFutureFunction[A, B, C](f: A => B Or C): A => Future[B] = {
    import OrConverters.OrFutureConverter
    f(_).toSemanticErrorFuture
  }

  implicit def eitherFunctionToSemanticErrorFutureFunction[A, L, R](f: A => Either[L, R]): A => Future[R] = {
    import EitherConverters.EitherFutureConverter
    f(_).toSemanticErrorFuture
  }

  implicit def tryFunctionToFutureFunction[A, B](f: A => Try[B]): A => Future[B] = {
    import TryConverters.TryFutureConverter
    f(_).toFuture
  }
}
object FunctionConverters extends FunctionConverters

object Converters extends OrConverters with TryConverters with FutureConverters with FunctionConverters
