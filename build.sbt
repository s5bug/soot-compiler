import scala.sys.process._

lazy val root = (project in file(".")).settings(
  organization := "tf.bug",
  name := "soot-compiler",
  version := "0.1.0",
  scalaVersion := "2.11.12",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "fastparse" % "1.0.0",
    "com.github.scopt" %%% "scopt" % "3.7.0"
  ),
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, "nativeVersion" -> nativeVersion, BuildInfoKey.action("buildTime") {
    s"${("""date""" :: """--utc""" :: """+%a, %b %d, %Y, %H:%M:%S.%N""" :: Nil).!!.trim} UTC"
  }),
  buildInfoPackage := "tf.bug.soot",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
).enablePlugins(ScalaNativePlugin, BuildInfoPlugin)
