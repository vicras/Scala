ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"
val zioVersion = "2.0.6"

lazy val compileDependencies = Seq(
  "dev.zio" %% "zio" % zioVersion
) map (_ % Compile)

lazy val root = (project in file("."))
  .settings(
    name := "tic-tac-toe",
    idePackagePrefix := Some("com.vicras"),
    libraryDependencies ++= compileDependencies
  )