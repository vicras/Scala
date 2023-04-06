package exception

import zhttp.http._

object ErrorHandler {
  def handle(ex: Throwable): UHttpApp = Http.succeed(matchError(ex))

  private def matchError(ex: Throwable): Response = {
    ex match {
      case exception.DutyAppException(message) => response(message, Status.BadRequest)
    }
  }

  private def response(message: String, status: Status): Response = {
    Response.text(message).setStatus(status)
  }
}
