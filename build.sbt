scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC1",

  "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % "0.16",

  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.3.0" % "test",
)

enablePlugins(GatlingPlugin)

enablePlugins(ParadoxRevealPlugin)
paradoxTheme := Some("net.bzzt" % "paradox-theme-reveal-js" % "0.1.0-SNAPSHOT")
