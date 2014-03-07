import sbt._
import Keys._

object ScalaAsyncErrorHandlingBuild extends Build {
  lazy val main = Project(
    id        = "main",
    base      = file( "." )
  )

  def scalaCompilerDependency(scalaVersion: String) = {
    Seq("org.scala-lang" % "scala-compiler" % scalaVersion % "test")
  }
}
