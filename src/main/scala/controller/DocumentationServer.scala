package controller

import exception.DocumentationException
import zio.UIO
import zio.http.HttpApp

trait DocumentationServer {
  def docsRoutes: UIO[HttpApp[Any, DocumentationException]]
}
