package com.example.electronapp

import scala.scalajs.js
import js.Dynamic.{global => g}
import io.atom.electron._
import scala.scalajs.js.timers._

object Main extends js.JSApp {

  def main(): Unit = {

    val electron = g.require("electron")
    val app = electron.app.asInstanceOf[App] // Module to control application life.

    // Keep a global reference of the window object, if you don't, the window will
    // be closed automatically when the JavaScript object is GCed.
    var mainWindow: BrowserWindow = null

    def createWindow(): Unit = {
      // Create the browser window.
      mainWindow = BrowserWindow(width = 800, height = 600)

      // and load the index.html of the app.
      mainWindow.loadURL(s"file://${g.__dirname}/index.html")

      // Open the devtools.
      mainWindow.openDevTools()

      // Emitted when the window is closed.
      val _ = mainWindow.on("closed", () =>
        // Dereference the window object, usually you would store windows
        // in an array if your app supports multi windows, this is the time
        // when you should delete the corresponding element.
        mainWindow = null
      )
    }

    // This method will be called when Electron has finished
    // initialization and is ready to create browser windows.
    app.on("ready", () => createWindow())

    // Quit when all windows are closed.
    app.on("window-all-closed", () => {
      // On OS X it is common for applications and their menu bar
      // to stay active until the user quits explicitly with Cmd + Q
      if (Process.platform != "darwin") {
        app.quit()
      }
    })

    app.on("activate", () => {
      // On OS X it's common to re-create a window in the app when the
      // dock icon is clicked and there are no other windows open.
      if (mainWindow == null) {
        createWindow()
      }
    })
  }
}
