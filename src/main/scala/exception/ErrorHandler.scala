package exception

import exception.CommonException.AuthenticationException
import zio.http._
import zio.http.model.Status
import zio.http.model.Status.{BadRequest, InternalServerError, Unauthorized}

object ErrorHandler {
  def handle(ex: Object): Response = ex match {
    case e: CommonException => matchError(e)
    case _ => response(ex.toString, InternalServerError)
  }

  private def matchError(ex: CommonException): Response = {
    ex match {
      case AuthenticationException(text) => response(text, Unauthorized)
      case CommonException.DocumentationException(exception) => response(exception, InternalServerError)
      case CommonException.AlreadyExistException(message) => response(message, BadRequest)
      case CommonException.InternalException(message) => response(message, InternalServerError)
    }
  }


  private def response(message: String, status: Status): Response = {
    Response.text(message).setStatus(status)
  }
}
