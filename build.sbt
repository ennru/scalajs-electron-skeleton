//import org.scalajs.sbtplugin.ScalaJSPlugin

name := "Scala.js Electron Skeleton"

scalaVersion in ThisBuild := V.scala

initialize := {
  val _ = initialize.value
  val required = VersionNumber("1.8")
  val current = VersionNumber(sys.props("java.specification.version"))
  assert(VersionNumber.Strict.isCompatible(current, required), s"Java $required required (detected $current).")
}


scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature")

lazy val shared = crossProject.crossType(CrossType.Pure).in(file("shared")).
  settings(
  )

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val backend = project.
  dependsOn(sharedJvm)

lazy val frontend = project.in(file("scalajs")).
  dependsOn(sharedJs)

lazy val root = project.in(file(".")).
  aggregate(backend, frontend)
