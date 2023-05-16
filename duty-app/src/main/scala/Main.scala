import controller.{DocumentationServer, PersonServer}
import exception.ErrorHandler
import jdbc.FlywayMigrator
import layer.env.DevLayer
import metric.PrometheusPublisherApp
import service.PersonBackup
import zio.Console.printLine
import zio._
import zio.http.{Server, ServerConfig}

object Main extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = (for {
    _ <- printLine("Welcome to Duty app!")
    _ <- FlywayMigrator.migrate

    _ <- printLine("Starting server...")

    backupFiber <- ZIO.serviceWithZIO[PersonBackup](_.doBackup).fork

    apiRoutes <- ZIO.serviceWithZIO[PersonServer](_.httpRoutes)
    docRoutes <- ZIO.serviceWithZIO[DocumentationServer](_.docsRoutes)
    zioMetrics <- PrometheusPublisherApp()
    _ <- Server.serve((apiRoutes ++ docRoutes ++ zioMetrics).mapError(ErrorHandler.handle))
  } yield ())
    .ensuring(ZIO.serviceWithZIO[PersonBackup](_.clear))
    .provide(DevLayer.dev,
      ServerConfig.live(ServerConfig.default.port(8080)),
      Server.live
    )
}