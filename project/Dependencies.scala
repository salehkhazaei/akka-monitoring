package ai.bale

import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {

  object V {
    val logback = "1.2.3"
    val akka = "2.5.14"
    val akkaHttp = "10.1.5"
    val gelfAppender = "1.8.0"
    val scalaLogging = "3.9.0"
    val scalatest = "3.0.4"
  }

  object Compile {
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging
    val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % V.akka
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.akka
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % V.akka
    val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % V.akka
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % V.akkaHttp
    val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % V.akka
    val logback = "ch.qos.logback" % "logback-classic" % V.logback
    val gelfAppender = "biz.paluch.logging" % "logstash-gelf" % V.gelfAppender
  }

  object Testing {
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % V.akka % Test
    val akkaMultiNodeTestkit = "com.typesafe.akka" %% "akka-multi-node-testkit" % V.akka

    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    val scalatest = "org.scalatest" %% "scalatest" % V.scalatest
  }

  import Compile._

  val l = libraryDependencies
  val shared = Seq(
    gelfAppender,
    logback,
    scalaLogging,
    akkaSlf4j,
    akkaActor,
    akkaStream,
    akkaCluster,
    akkaClusterSharding
  )

  val core = l ++= shared

  import Testing._

  val test = l ++= shared ++ Seq(
    scalatest,
    akkaTestkit
  )
}