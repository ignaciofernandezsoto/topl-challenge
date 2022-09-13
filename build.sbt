ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "topl-challenge"
  )

val circeVersion = "0.14.2"
val catsEffectVersion = "3.3.14"
val duckTapeVersion = "0.1.0-RC1"

val scalaTestVersion = "3.2.12"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "io.github.arainko" %% "ducktape" % duckTapeVersion,
) ++ Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerBaseImage := "openjdk:8-jdk"