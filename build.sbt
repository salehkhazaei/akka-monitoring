import Keys._
import ai.bale.Dependencies
import ai.bale.Build._

fork in run := true
javaOptions in run += "-Djdk.logging.allowStackWalkSearch=true"
connectInput in run := true

enablePlugins(JavaServerAppPackaging)
enablePlugins(DockerPlugin)

bashScriptExtraDefines += """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml""""

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

Compile / paradoxMaterialTheme := {
  ParadoxMaterialTheme()
    .withColor("teal", "green")
    .withRepository(uri("https://github.com/salehkhazaei/akka-monitoring"))
}

lazy val root = Project(
  id = "akka-monitoring-service",
  base = file(".")
).aggregate(core, test)
  .settings(
    dockerBaseImage := "openjdk",
    packageName in Docker := "registry.bale.ai:2443/akka-monitoring",
    version in Docker := (version in ThisBuild).value,
    dockerExposedPorts := Seq(9080),
    dockerUpdateLatest := true
  )

lazy val core = module("monitoring-core")
  .settings(Dependencies.core)

lazy val test = module("monitoring-test")
  .settings(Dependencies.test)
  .dependsOn(core)

lazy val docs = module("monitoring-docs")
  .enablePlugins(ParadoxMaterialThemePlugin)

def module(name: String): Project =
  Project(id = name, base = file(name))
    .settings(buildSettings)
    .settings(defaultSettings)