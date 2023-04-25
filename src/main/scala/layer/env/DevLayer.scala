package layer.env

import auth.BasicAuthChecker
import controller.routes.OpenApiDocsServer
import controller.{DocumentationServer, PersonServer}
import layer._
import metric.PrometheusPublisherApp
import org.flywaydb.core.api.configuration.FluentConfiguration
import zio.ZLayer
import zio.metrics.connectors.prometheus.PrometheusPublisher

object DevLayer {
  val dev: ZLayer[Any, Nothing, FluentConfiguration with DocumentationServer with PersonServer with PrometheusPublisher] =
    ZLayer.make[FluentConfiguration with DocumentationServer with PersonServer with PrometheusPublisher](
      DatasourceLayer.hikariConfig,
      DatasourceLayer.hikariDataSource,
      EnvVariableLayer.configFactory,
      FlywayLayer.flywayConfiguration,
      QuillLayer.quillPostgresCtx,
      RepositoryLayer.repos,
      ServiceLayer.services,
      ServiceLayer.passwordEncoder,
      ControllerLayer.controllers,
      BasicAuthChecker.live,
      OpenApiDocsServer.live,
      PrometheusPublisherApp.live,
      PrometheusPublisherApp.livePrometheusLayer
    )
}
