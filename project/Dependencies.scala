import sbt._

object V {
  val scala = "2.11.8"
  val akka = "2.4.7"
  val upickle = "0.4.1"
}

object Dependencies {
  val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % V.akka
  val upickle = "com.lihaoyi" %% "upickle" % V.upickle
}