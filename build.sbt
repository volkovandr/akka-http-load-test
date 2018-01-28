name := "akka-http-load-test"

version := "0.1"

scalaVersion := "2.12.4"

organization := "my.volkovandr"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-actor" % "2.4.20",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11",
  "com.typesafe" % "config" % "1.3.2",
  "io.prometheus" % "simpleclient" % "0.1.0",
  "io.prometheus" % "simpleclient_httpserver" % "0.1.0",
)