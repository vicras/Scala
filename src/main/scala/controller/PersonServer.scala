package controller

import zio.ZIO
import zio.http.HttpApp

trait PersonServer {
  def httpRoutes: ZIO[Any, Nothing, HttpApp[Any, Throwable]]
}
