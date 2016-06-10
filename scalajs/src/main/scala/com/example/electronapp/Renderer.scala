package com.example.electronapp

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js
import js.Dynamic.{global => g}
import js.annotation.JSExport

@JSExport
object Renderer {

  val fs = g.require("fs")

  @JSExport
  def main(body: html.Div): Unit = {
    val filenames = listFiles(".")
    ReactDOM.render(Components.display(filenames), body)
  }

  def listFiles(path: String): Seq[String] = {
    fs.readdirSync(path).asInstanceOf[js.Array[String]]
  }
}

import japgolly.scalajs.react.ReactComponentB

object Components {

  import japgolly.scalajs.react.vdom.prefix_<^._

  val display =
    ReactComponentB[Seq[String]]("display")
      .render_P(filenames => {
        <.div(
          <.p("Hello World from Scala.js."),
          <.p("Listing the files in the '.' using node.js API:"),
          <.ul(filenames.map(<.li(_)))
        )
      })
      .build

}