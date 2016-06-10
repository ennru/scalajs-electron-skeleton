import java.nio.charset.Charset

name := "Scala.js Electron Skeleton"

scalaVersion in ThisBuild := "2.11.8"

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature")

lazy val frontend = project.in(file("scalajs"))

