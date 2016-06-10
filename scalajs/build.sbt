import java.nio.charset.Charset

enablePlugins(ScalaJSPlugin)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.0",
  "com.github.japgolly.scalajs-react" %%% "core" % "0.11.1"
)

jsDependencies ++= Seq(
  RuntimeDOM,
  "org.webjars.bower" % "react" % "15.0.2"
    /        "react-with-addons.js"
    minified "react-with-addons.min.js"
    commonJSName "React",
  "org.webjars.bower" % "react" % "15.0.2"
    /         "react-dom.js"
    minified  "react-dom.min.js"
    dependsOn "react-with-addons.js"
    commonJSName "ReactDOM",
  "org.webjars.bower" % "react" % "15.0.2"
    /         "react-dom-server.js"
    minified  "react-dom-server.min.js"
    dependsOn "react-dom.js"
    commonJSName "ReactDOMServer"
)

skip in packageJSDependencies := false

persistLauncher in Compile := true
persistLauncher in Test := false

val electronMainPath = SettingKey[File]("electron-main-path", "The absolute path where to write the Electron application's main.")
val electronMain = TaskKey[File]("electron-main", "Generate Electron application's main file.")

electronMainPath := baseDirectory.value / ".." / "electron-app"

// we generate the code for electron's main by aggregating the fastOptJS code, the launcher code and a little hack for the global stuff
// we do not include the jsDependencies in the main (for now)
electronMain := {
  // TODO here we rely on the files written on disk but it would be better to be able to get the actual content directly
  // from the tasks. I am not sure it's possible just yet though.
  val fastOptFile = (fastOptJS in Compile).value.data
  val jsCode: String = IO.read(fastOptFile, Charset.forName("UTF-8"))
  val launchCode = IO.read((packageScalaJSLauncher in Compile).value.data, Charset.forName("UTF-8"))
  // we don't need jsDeps here but want it to be generated anyway so that we can start the Electron app right away
  val jsDeps = (packageJSDependencies in Compile).value

  // hack to get require and __dirname to work in the main process
  // see https://gitter.im/scala-js/scala-js/archives/2015/04/25
  val hack = """
  |var addGlobalProps = function(obj) {
  |  obj.require = require;
  |  obj.__dirname = __dirname;
  |}
  |
  |if((typeof __ScalaJSEnv === "object") && typeof __ScalaJSEnv.global === "object") {
  |  addGlobalProps(__ScalaJSEnv.global);
  |} else if(typeof  global === "object") {
  |  addGlobalProps(global);
  |} else if(typeof __ScalaJSEnv === "object") {
  |  __ScalaJSEnv.global = {};
  |  addGlobalProps(__ScalaJSEnv.global);
  |} else {
  |  var __ScalaJSEnv = { global: {} };
  |  addGlobalProps(__ScalaJSEnv.global)
  |}
  |""".stripMargin

  val dest = electronMainPath.value
  IO.write(dest / "main.js", hack + jsCode + launchCode, Charset.forName("UTF-8"))
  IO.copyFile(fastOptFile, dest / "frontend-fastopt.js")
  IO.copyFile(jsDeps, dest / "frontend-jsdeps.js")
  dest
}
