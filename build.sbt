ThisBuild / scalaVersion := "2.13.10"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.vicras"
ThisBuild / organizationName := "example"

libraryDependencies += Dependencies.zio
libraryDependencies ++= Dependencies.rest

libraryDependencies += Dependencies.bcrypt
libraryDependencies += Dependencies.postgresqlDriver
libraryDependencies += Dependencies.quill
libraryDependencies ++= Dependencies.doobie
libraryDependencies ++= Dependencies.flyway
libraryDependencies += Dependencies.log4j
libraryDependencies += Dependencies.logback
libraryDependencies ++= Dependencies.zioLogging

libraryDependencies += Dependencies.testing

