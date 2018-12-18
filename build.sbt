name := "image-resize-service"

organization := "com.socrata"

version := "0.0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Socrata Artifactory" at "https://repo.socrata.com/artifactory/libs-release"
)

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.socrata" %% "socrata-http-jetty" % "3.11.4",
  "org.imgscalr" % "imgscalr-lib" % "4.2"
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

// WARNING: -optimize is not recommended with akka, should that come up.
// NOTE: Having to remove -Xfatal-warnings because it chokes due to inliner issues.
// This really bothers me.
scalacOptions ++= Seq("-optimize", "-deprecation", "-feature", "-language:postfixOps", "-Xlint")
