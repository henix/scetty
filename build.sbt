organization := "info.henix"

name := "scetty"

description := "Scala async http client based on jetty-client"

version := "0.3"

licenses := Seq("3-clause BSD" -> url("http://opensource.org/licenses/BSD-3-Clause"))

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-Yno-adapted-args")

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-client" % "9.4.8.v20171121"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/henix/scetty</url>
  <scm>
    <url>git@github.com:henix/scetty.git</url>
    <connection>scm:git:git@github.com:henix/scetty.git</connection>
  </scm>
  <developers>
    <developer>
      <id>henix</id>
      <name>henix</name>
      <url>https://github.com/henix</url>
    </developer>
  </developers>
)
