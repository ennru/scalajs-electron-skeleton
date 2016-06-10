package com.example.electronapp

import scala.scalajs.js
import org.scalajs.jquery.jQuery

import js.Dynamic.{global => g}
import js.annotation.JSExport

@JSExport
object Renderer {

  val fs = g.require("fs")


  @JSExport
  def main(): Unit = {
    val body = jQuery("body")
    val filenames = listFiles(".")
    body.append("<p>Hello World from Scala.js</p>" + display(filenames))
  }

  def display(filenames: Seq[String]): String = {
    val sb = new StringBuilder
    sb.append("<p>Listing the files in the '.' using node.js API:")
    sb.append("<ul>")
    filenames.foreach { filename =>
      sb.append(s"<li>$filename</li>")
    }
    sb.append("</ul></p>")
    sb.toString()
  }

  def listFiles(path: String): Seq[String] = {
    fs.readdirSync(path).asInstanceOf[js.Array[String]]
  }
}
