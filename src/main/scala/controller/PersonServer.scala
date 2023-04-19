package controller

import zio.UIO
import zio.http.HttpApp

trait PersonServer extends Documented {
  def httpRoutes: UIO[HttpApp[Any, Throwable]]
}
