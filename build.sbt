name := "scetty"

organization := "henix"

version := "0.1-SNAPSHOT"

licenses := Seq("3-clause BSD" -> url("http://opensource.org/licenses/BSD-3-Clause"))

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-Yno-adapted-args")

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-client" % "9.2.5.v20141112"
)
