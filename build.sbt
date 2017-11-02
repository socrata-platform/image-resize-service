name := "image-resize-service"

organization := "com.socrata"

version := "0.0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "socrata maven" at "https://repository-socrata-oss.forge.cloudbees.com/release"
)

libraryDependencies ++= Seq(
  "com.socrata" %% "socrata-http-jetty" % "3.11.1",
  "org.slf4j" % "slf4j-log4j12" % "1.7.21",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.imgscalr" % "imgscalr-lib" % "4.2"
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

// WARNING: -optimize is not recommended with akka, should that come up.
// NOTE: Having to remove -Xfatal-warnings because it chokes due to inliner issues.
// This really bothers me.
scalacOptions ++= Seq("-optimize", "-deprecation", "-feature", "-language:postfixOps", "-Xlint")
