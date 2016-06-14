Scala.js and Akka http skeleton with React to target Electron apps
==================================================================

This is an example of how to use [React](https://facebook.github.io/react/index.html) and [Scala.js](http://www.scala-js.org/) (using [scalajs-react](https://github.com/japgolly/scalajs-react)) 
together with [Electron](http://electron.atom.io/) to build native applications with a backend based on [Akka http](http://akka.io/).

## Getting started
Start one shell for the backend Akka server 
``` bash
sbt
sbt> backend/run
```

And one shell for the frontend code and launch the electronMain task
``` bash
sbt
sbt> ~electronMain
```

Then you will need to get Electron [downloaded](https://github.com/atom/electron/releases)  on your machine.

Once you do, call the Electron executable with the electron-app subfolder as a argument:
``` bash
/your/path/to/Electron electron-app
# e.g. on my MacOS machine
~/development/tools/electron-v1.1.1-darwin-x64/Electron.app/Contents/MacOS/Electron electron-app/
```

You should see a new window opening with the following text:
> Hello World!
>
>We are using node.js v6.1.0 and Electron 1.2.2.
>
>Hello World from Scala.js

followed by a list of files in the local (client!) directory.

## Main process
Sbt task `electronMain` aggregates the content of `fastOptJS` and of the launcher to form the 
`main.js` file that will be provided to Electron's main process. This is why the repo does not 
contain a `main.js` under the `electron-app` folder (unlike electron's quick start example): 
it is generated from the Scala.js code.

Electron's [main process]((http://electron.atom.io/docs/tutorial/quick-start/)) is implemented 
by 'com.example.electronapp.Main.scala']. It extends `js.App` and that's what the generated Scala.js 
launcher launches. You should not extend `js.App` elsewhere in your code or that will generate a conflict: instead use `JSExport`.

## Renderer process
The javascript code loaded from within the rendered process (i.e. `index.html`) is implemented in
`com.example.electronapp.Renderer.scala` and uses the `JSExport` annotation to be callable from 
javascript and its main method is explicitly called from within `index.html`.

## Copyright
Copyright © 2016 Enno Runne, (base on work by [Boris Chazalet](https://github.com/bchazalet/scalajs-electron-skeleton))

This work is free. You can redistribute it and/or modify it under the
terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See the COPYING file for more details.
