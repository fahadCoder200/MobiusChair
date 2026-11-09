name := "CPU-Design"
version := "0.1"
scalaVersion := "2.13.15"

libraryDependencies ++= Seq(
  "org.chipsalliance" %% "chisel" % "6.7.0"
)

// This plugin lets Chisel's macros and circt package bindings function correctly
addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % "6.7.0" cross CrossVersion.full)

scalacOptions ++= Seq(
  "-language:reflectiveCalls",
  "-deprecation",
  "-feature",
  "-Xcheckinit"
)
