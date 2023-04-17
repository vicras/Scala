package exception

import zio.http._
import zio.http.model.Status

object ErrorHandler {
  def handle(ex: Throwable): Response = matchError(ex)

  private def matchError(ex: Throwable): Response = {
    ex match {
      case exception.CommonException(message) => response(message, Status.BadRequest)
    }
  }

  private def response(message: String, status: Status): Response = {
    Response.text(message).setStatus(status)
  }
}
