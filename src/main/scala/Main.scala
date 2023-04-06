import controller.PersonApp
import jdbc.FlywayMigrator
import layer.env.DevLayer
import zhttp.http.Middleware
import zhttp.service.Server
import zio.Console.printLine
import zio._

object Main extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = (for {
    _ <- printLine("Welcome to Duty app!")
    _ <- FlywayMigrator.migrate

    _ <- printLine("Starting server...")
    personController <- ZIO.service[PersonApp]
    _ <- Server.start(8080, personController.app @@ Middleware.debug)
  } yield ())
    .provide(DevLayer.dev)
}