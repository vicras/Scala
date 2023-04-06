package layer

import com.typesafe.config.Config
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import zio.{ZIO, ZLayer}

import javax.sql.DataSource

object FlywayLayer {

  val flywayConfiguration: ZLayer[Config with DataSource, Nothing, FluentConfiguration] = ZLayer.fromZIO(flywayFluentConfig)

  private def flywayFluentConfig: ZIO[Config with DataSource, Nothing, FluentConfiguration] = for {
    config <- ZIO.service[Config]
    datasource <- ZIO.service[DataSource]
    fluent <- ZIO.succeed(Flyway.configure
      .loggers("log4j2")
      .dataSource(datasource)
      .schemas("public")
      .group(true)
      .outOfOrder(false)
      .locations(config.getString("duty-app.database.liquibase.scripts"))
      .ignoreMigrationPatterns("*:pending")
      .failOnMissingLocations(true)
      .baselineOnMigrate(true))
  } yield fluent
}
