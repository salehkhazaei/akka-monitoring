package ai.bale

import sbt.Keys._
import sbt._

object Build  {
  val ScalaVersion = "2.12.8"
  lazy val buildSettings =
    Seq(
      scalaVersion := ScalaVersion,
      scalaVersion in ThisBuild := ScalaVersion,
      crossPaths := false,
      organization := "ai.bale",
      organizationHomepage := Some(url("https://bale.ai")),
      parallelExecution := true,
      publishArtifact in packageDoc := false,
      autoCompilerPlugins := true,
      scalacOptions in (Compile, doc) ++= Seq(
        "-groups",
        "-implicits",
        "-diagrams"
      ),
      credentials += Credentials(Path.userHome / ".credentials"),
      publishTo := Some("Artifactory Realm" at "https://artifactory.bale.ai:3443/artifactory/ai.bale")
    )

  lazy val defaultSettings = Seq(
    initialize ~= { _ ⇒
      if (sys.props("java.specification.version") != "1.8")
        sys.error("Java 8 is required for this project.")
    },
    fork in Test := false
  )
}