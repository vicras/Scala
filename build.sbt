ThisBuild / scalaVersion := "2.13.10"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.vicras"
ThisBuild / organizationName := "example"

libraryDependencies += Dependencies.zio
libraryDependencies += Dependencies.zioStreams
libraryDependencies += Dependencies.metrics
libraryDependencies ++= Dependencies.rest
libraryDependencies ++= Dependencies.db
libraryDependencies ++= Dependencies.logs
libraryDependencies ++= Dependencies.tests
libraryDependencies += Dependencies.bcrypt
