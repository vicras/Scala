package controller

import sttp.tapir.ztapir.ZServerEndpoint
import zio.UIO

trait Documented {
  def endpoints: UIO[List[ZServerEndpoint[Any, Nothing]]]
}
