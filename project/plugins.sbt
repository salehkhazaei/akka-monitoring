logLevel := sbt.Level.Warn
credentials += Credentials(Path.userHome / ".credentials")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2-RC2")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("io.github.jonas" % "sbt-paradox-material-theme" % "0.5.1")