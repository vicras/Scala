package controller.routes

import controller.{DocumentationServer, Documented, PersonServer}
import exception.CommonException.DocumentationException
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir.ZServerEndpoint
import zio.http.{Http, Request, Response}
import zio.{Task, UIO, ZIO, ZLayer}

class OpenApiDocsServer(servicesToBeDocumented: List[Documented]) extends DocumentationServer {
  def docsRoutes: ZIO[Any, Nothing, Http[Any, DocumentationException, Request, Response]] = for {
    endpoints <- getAllEndpointsForDocumentation
    openApi <- ZIO.succeed(OpenAPIDocsInterpreter().serverEndpointsToOpenAPI(endpoints, "Duty app Server", "0.1"))
    openApiHttp <- ZIO.succeed(ZioHttpInterpreter().toHttp(SwaggerUI[Task](openApi.toYaml)))
  } yield openApiHttp.mapError(e => new DocumentationException(e.getMessage))

  private def getAllEndpointsForDocumentation: UIO[List[ZServerEndpoint[Any, Nothing]]] =
    ZIO.foreach(servicesToBeDocumented)(_.endpoints).map(_.flatten)
}

object OpenApiDocsServer {
  def live: ZLayer[PersonServer, Nothing, DocumentationServer] = liveDocumentedList >>> liveDocService

  private def liveDocService: ZLayer[List[Documented], Nothing, DocumentationServer] =
    ZLayer.fromFunction((documented: List[Documented]) => new OpenApiDocsServer(documented))

  private def liveDocumentedList: ZLayer[PersonServer, Nothing, List[Documented]] = {
    ZLayer.fromFunction((personServer: PersonServer) => List(personServer))
  }
}
