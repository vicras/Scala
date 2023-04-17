package layer.env

import auth.PasswordEncoder
import controller.{PersonApp, PersonServer}
import layer._
import org.flywaydb.core.api.configuration.FluentConfiguration
import repository.PersonRepo
import routes.PersonRoutes
import zio.ZLayer

object DevLayer {
  val dev: ZLayer[Any, Nothing, FluentConfiguration with PersonRepo with PersonServer with PasswordEncoder] =
    ZLayer.make[FluentConfiguration with PersonRepo with PersonServer with PasswordEncoder](
      DatasourceLayer.hikariConfig,
      DatasourceLayer.hikariDataSource,
      EnvVariableLayer.configFactory,
      FlywayLayer.flywayConfiguration,
      QuillLayer.quillPostgresCtx,
      RepositoryLayer.repos,
      ServiceLayer.services,
      ServiceLayer.passwordEncoder,
      ControllerLayer.controllers,
      PersonRoutes.live
    )
}
