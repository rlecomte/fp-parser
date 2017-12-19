import Dependencies._

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.rlecomte",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "fp parser",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-RC1"
  )
