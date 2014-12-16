name := "scetty"

organization := "henix"

version := "0.2.1"

licenses := Seq("3-clause BSD" -> url("http://opensource.org/licenses/BSD-3-Clause"))

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-Yno-adapted-args")

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-client" % "9.2.6.v20141205"
)
