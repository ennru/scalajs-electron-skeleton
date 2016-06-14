package backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{Message, TextMessage, UpgradeToWebSocket}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import shared.Protocol._
import upickle.default._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object BootAkkaHttp extends App {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val serverSource = Http().bind(interface = "localhost", port = 8080)

  val webSocket = Flow[Message].
    collect {
      case TextMessage.Strict(text) =>
        println(s"websocket received: $text")
        read[ProtocolBase](text)
    } map {
      case Hello(msg) =>
        TextMessage.Strict(write(Hello(msg + " dude!")))

      case res =>
        TextMessage.Strict(write(s"Error: unhandled message $res"))
    }

  val requestHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      val res = write(Hello("World!"))
      Future.successful(HttpResponse(400, entity = res))

    case HttpRequest(GET, Uri.Path("/json"), _, entity, _) if !entity.isKnownEmpty =>
      for {
        content <- entity.toStrict(200.milliseconds)
      } yield {
        val base = read[ProtocolBase](content.data.utf8String)
        base match {
          case Hello(m) =>
            val res = Hello(s"Nice to meet you! $m")
            HttpResponse(entity = HttpEntity(
              ContentTypes.`application/json`, write(res)
            ))
        }
      }

    case req @ HttpRequest(GET, Uri.Path("/ws"), _, _, _) =>
      req.header[UpgradeToWebSocket] match {
        case Some(upgrade) => Future(upgrade.handleMessages(webSocket))
        case None          => Future.successful(HttpResponse(400, entity = "Not a valid websocket request!"))
      }

    case HttpRequest(GET, Uri.Path("/terminate"), _, _, _) =>
      system.terminate()
      sys.error("Backend terminated")

    case _: HttpRequest =>
      Future.successful(HttpResponse(404, entity = "Unknown resource!"))
  }


  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)
      connection handleWithAsyncHandler requestHandler
    }).run()

}