import controller.PersonServer
import jdbc.FlywayMigrator
import layer.env.DevLayer
import zio.Console.printLine
import zio._
import zio.http.{Server, ServerConfig}

object Main extends ZIOAppDefault {
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = (for {
    _ <- printLine("Welcome to Duty app!")
    _ <- FlywayMigrator.migrate

    _ <- printLine("Starting server...")
    routes <- ZIO.serviceWithZIO[PersonServer](_.httpRoutes)
    _ <- Server.serve(routes.withDefaultErrorResponse)
  } yield ())
    .provide(DevLayer.dev,
      ServerConfig.live(ServerConfig.default.port(8081)),
      Server.live
    )
}