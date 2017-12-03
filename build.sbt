enablePlugins(PackPlugin)
enablePlugins(DockerPlugin)

name := "feabana"
organization := "com.datagemme"
version := "1.0"
scalaVersion := "2.12.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.5.3"
  val akkaHttpV   = "10.0.9"
  val scalaTestV  = "3.0.1"
  val elastic4sVersion = "5.5.5"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion
  )
}

val circeVersion = "0.8.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)

packMain := Map("feabana" -> "main.Feabana")

/* DOCKER */
dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = new File("target/pack")
  val artifactTargetPath = s"/app"

  new Dockerfile {
    from("openjdk")
    expose(5700)
    add(artifact, artifactTargetPath)
    entryPoint("/bin/bash")
    cmd("./app/bin/feabana")
  }
}
imageNames in docker := Seq(
  // Sets the latest tag
  ImageName(s"datagemme/feabana:latest"),
  ImageName(s"datagemme/feabana:v"+version.value)
)
buildOptions in docker := BuildOptions(cache = false)

