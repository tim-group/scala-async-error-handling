name := "scala-async-error-handling"

organization := "com.timgroup"

version := "0.0.1"

scalaVersion := "2.10.7"

crossScalaVersions := Seq("2.10.7")

libraryDependencies <++= scalaVersion(scalaCompilerDependency(_))

libraryDependencies += "org.scalautils" %% "scalautils" % "2.1.0" % "optional"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.0" % "test"

publishMavenStyle := true

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/youdevise/scala-async-error-handling</url>
  <licenses>
    <license>
      <name>MIT license</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:youdevise/scala-async-error-handling.git</url>
    <connection>scm:git:git@github.com:youdevise/scala-async-error-handling.git</connection>
  </scm>
  <developers>
    <developer>
      <id>broberts</id>
      <name>Brian Roberts</name>
      <email>brian.roberts@timgroup.com</email>
    </developer>
    <developer>
      <id>ms-tg</id>
      <name>Marc Siegel</name>
      <email>marc.siegel@timgroup.com</email>
    </developer>
  </developers>
)
