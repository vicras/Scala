import sbt._

object Dependencies {

  object Versions {
    val flyway = "9.4.0"

    val log4j = "2.20.0"
    val logback = "1.4.5"

    val postgresql = "42.5.4"
    val quill = "4.6.0"
    val doobie = "1.0.0-RC2"

    val zio = "2.0.9"
    val zioMetrics = "2.0.7"
    val json = "0.4.2"
    val http = "0.0.5"
    val bcrypt = "0.10.2"

    val test = "2.0.9"

    val zioConfig = "3.0.2"
    val zioLogging = "2.1.2"
    val zioTapir = "1.2.12"
  }

  // ZIO
  val zio = "dev.zio" %% "zio" % Versions.zio
  val metrics = "dev.zio" %% "zio-metrics-connectors" % Versions.zioMetrics

  // REST
  val http = "dev.zio" %% "zio-http" % Versions.http
  val json = "dev.zio" %% "zio-json" % Versions.json

  val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-zio" % Versions.zioTapir
  val tapirSwaggerUI ="com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.zioTapir
  val tapirZioServer = "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % Versions.zioTapir
  val tapirZioJson = "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % Versions.zioTapir
  val tapirCircle = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Versions.zioTapir
  val tapirZioMetrics = "com.softwaremill.sttp.tapir" %% "tapir-zio-metrics" % Versions.zioTapir

  // TAPIR (Endpoints, Documentation, Swagger)
  val tapir: Seq[ModuleID] = Seq(tapirCore, tapirSwaggerUI, tapirZioServer, tapirZioJson, tapirZioMetrics)

  val rest: Seq[ModuleID] = Seq(http, json) ++ tapir

  // AUTH
  val bcrypt = "at.favre.lib" % "bcrypt" % Versions.bcrypt

  // Database
  val postgresqlDriver = "org.postgresql" % "postgresql" % Versions.postgresql
  val quill = "io.getquill" %% "quill-jdbc-zio" % Versions.quill

  val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-h2"
  ).map(_ % Versions.doobie)

  val flyway: Seq[ModuleID] = Seq(
    "org.flywaydb" % "flyway-core",
    "org.flywaydb" % "flyway-maven-plugin"
  ).map(_ % Versions.flyway)

  val db: Seq[ModuleID] = Seq(postgresqlDriver, quill) ++ doobie ++ flyway

  // Logging
  val log4j = "org.apache.logging.log4j" % "log4j-core" % Versions.log4j
  val logback = "ch.qos.logback" % "logback-classic" % Versions.logback
  val zioLogging: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio-logging",
    "dev.zio" %% "zio-logging-slf4j"
  ).map(_ % Versions.zioLogging)

  val logs: Seq[ModuleID] = Seq(log4j, logback) ++ zioLogging
  // Tests
  val testing = "dev.zio" %% "zio-test" % Versions.test % Test
}
