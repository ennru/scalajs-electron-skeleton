package com.example.electronapp

import japgolly.scalajs.react.CallbackTo.MapGuard
import japgolly.scalajs.react._
import org.scalajs.dom.html
import org.scalajs.dom.raw._
import shared.Protocol._
import upickle.default._

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport

@JSExport
object Renderer {

  val fs: Dynamic = g.require("fs")

  @JSExport
  def main(div1: html.Div, div2: html.Div): Unit = {
    val filenames = listFiles(".")
    ReactDOM.render(Components.display(filenames), div1)

    val inChannel = new EventChannel[ProtocolBase]

    val chat = new WebSocket("ws://localhost:8080/ws")
    chat.onopen = { (event: Event) ⇒
      println("onopen")
      event.preventDefault()
    }
    chat.onerror = { (event: ErrorEvent) ⇒
      println("onerror")
    }
    chat.onmessage = { (event: MessageEvent) ⇒
      val wsMsg = read[ProtocolBase](event.data.toString)
      println(s"received $wsMsg")
      inChannel.onMessage(wsMsg)
    }
    chat.onclose = { (event: Event) ⇒
      println("onclose")
    }

    val outChannel = new EventChannel[ProtocolBase]
    outChannel.subscribe(new Receiver[ProtocolBase] {
      override def onMessage(msg: ProtocolBase): Unit = chat.send(write(msg))
    })

    val channels = BidirChannel(inChannel, outChannel)
    ReactDOM.render(WsComponent.Messages(channels), div2)
  }

  def listFiles(path: String): Seq[String] = {
    fs.readdirSync(path).asInstanceOf[js.Array[String]]
  }
}

import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object Components {


  val display =
    ReactComponentB[Seq[String]]("display")
      .render_P { filenames =>
        <.div(
          <.p("Hello World from Scala.js."),
          <.p("Listing the files in the '.' using node.js API:"),
          <.ul(filenames.map(<.li(_)))
        )
      }
      .build

}

object WsComponent {

  type Props = BidirChannel[ProtocolBase]
  case class State(text: String, received: List[String]) {
    def received(text: String): State = {
      copy(received = s"$text (${new java.util.Date()})" :: received)
    }
  }

  class Backend($: BackendScope[Props, State]) {

    def onTextChange(e: ReactEventI) =
      e.extract(_.target.value)(value => $.modState(_.copy(text = value)))

    def send(msg: ProtocolBase): Callback = $.props.map(_.out.onMessage(msg))
    def sendText: State => Callback = (s: State) => send(Hello(s.text))
    def clearText: Callback = $.modState(s => s.copy(text = ""))

    def onClick(event: ReactEventI): Callback = {
      ($.state >>= sendText) >> clearText
    }

    def render(p: Props, s: State) =
      <.div(
        <.p("Chat with Akka on the server side"),
        <.ul(s.received.map(<.li(_))),
        <.input.text(
          ^.placeholder := "Say something nice...",
          ^.value := s.text,
          ^.onChange ==> onTextChange),
        <.button(^.onClick ==> onClick, "Send")
      )
  }

  val Messages =
    ReactComponentB[Props]("messages")
      .initialState(State("", Nil))
      .renderBackend[Backend]
      .componentDidMount { f =>
        f.props.in.subscribe(new Receiver[ProtocolBase] {
          override def onMessage(msg: ProtocolBase): Unit = {
            println(s"received $msg")
            msg match {
              case Hello(text) => f.modState(s => s.received(text)).runNow()
              case msg: ProtocolBase => f.modState(s => s.copy(received = msg.toString :: s.received)).runNow()
            }

          }
        })
        f.modState(s => s.received("started"))
      }
      .build
}