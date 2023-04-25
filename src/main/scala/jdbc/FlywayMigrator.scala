package jdbc

import exception.MigrationException
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.output.ValidateResult
import zio._

import scala.jdk.CollectionConverters._


object FlywayMigrator {

  def migrate: ZIO[FluentConfiguration, MigrationException, Unit] =
    for {
      _ <- ZIO.logInfo(s"Starting the migration")
      count <- migrationEffect()
      _ <- ZIO.logInfo(s"Successful migrations: $count")
    } yield ()

  private def migrationEffect(): ZIO[FluentConfiguration, MigrationException, Int] =
    for {
      flywayConfig <- ZIO.service[FluentConfiguration]
      _ <- logValidationErrorsIfAny(flywayConfig)
      count <- migrationsAppliedCount(flywayConfig)
      _ <- logMigrationResults(flywayConfig)
    } yield count

  private def migrationsAppliedCount(flywayConfig: FluentConfiguration): ZIO[Any, Nothing, RuntimeFlags] = {
    ZIO.succeed(flywayConfig.load().migrate().migrationsExecuted)
  }

  private def logMigrationResults(flywayConfig: FluentConfiguration): ZIO[Any, Nothing, List[Unit]] = {
    ZIO.foreach(flywayConfig.load().info().all().toList) { migrationInfo =>
      ZIO.logInfo(s"Migration ${migrationInfo.getDescription} status is ${migrationInfo.getType.toString}")
    }
  }

  private def logValidationErrorsIfAny(flywayConfig: FluentConfiguration): ZIO[Any, MigrationException, Unit] = for {
    validated <- ZIO.succeed(flywayConfig.load().validateWithResult)
    _ <- logMigrationErrorEffect(validated).unless(validated.validationSuccessful)
  } yield ()

  private def logMigrationErrorEffect(validationResult: ValidateResult): ZIO[Any, MigrationException, Nothing] = {
    val errorMessage = validationResult.invalidMigrations.asScala.map(error => s"Invalid migration: ${error.errorDetails.errorMessage}").mkString(" ")
    ZIO.logError(errorMessage) *> ZIO.fail(new MigrationException("Migrations validation failed (see the logs)"))
  }
}