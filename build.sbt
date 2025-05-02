import sbtassembly.AssemblyKeys.assembly
import sbtassembly.{MergeStrategy, PathList}

name := "image-resize-service"

organization := "com.socrata"

version := "0.0.1"

scalaVersion := "2.12.12"

resolvers ++= Seq(
  "Socrata Artifactory" at "https://repo.socrata.com/artifactory/libs-release"
)

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.3.15",
  "com.socrata" %% "socrata-http-jetty" % "3.16.0",
  "org.imgscalr" % "imgscalr-lib" % "4.2"
)

Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

// WARNING: -optimize is not recommended with akka, should that come up.
// NOTE: Having to remove -Xfatal-warnings because it chokes due to inliner issues.
// This really bothers me.
scalacOptions ++= Seq("-optimize", "-deprecation", "-feature", "-language:postfixOps", "-Xlint")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) =>
    (xs.map{_.toLowerCase}) match {
      case "maven" :: xs => MergeStrategy.discard
      case "manifest.mf" :: nil => MergeStrategy.discard
      case "index.list" :: nil => MergeStrategy.discard
      case head :+ "module-info.class" => MergeStrategy.discard
      case _ => MergeStrategy.deduplicate
    }
  case "module-info.class" => MergeStrategy.discard
  case other => MergeStrategy.defaultMergeStrategy(other)
}
