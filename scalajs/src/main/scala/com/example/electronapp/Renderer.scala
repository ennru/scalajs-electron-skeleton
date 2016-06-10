package com.example.electronapp

import org.scalajs.dom

import scala.scalajs.js

import js.Dynamic.{global => g}
import js.annotation.JSExport

@JSExport
object Renderer {

  val fs = g.require("fs")


  @JSExport
  def main(): Unit = {
    val body = dom.document.getElementById("anchor")
    val filenames = listFiles(".")
    body.textContent = "<p>Hello World from Scala.js</p>" + display(filenames)
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
