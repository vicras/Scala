package controller

import zio.UIO
import zio.http.App

trait PersonServer extends Documented {
  def httpRoutes: UIO[App[Any]]
}
