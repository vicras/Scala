package auth

import domain.{Email, Person}
import repository.PersonRepo
import zio.http.RequestHandlerMiddleware
import zio.http.middleware.RequestHandlerMiddlewares
import zio.{URIO, ZIO}

object BasicAuthMiddleware {
  val basicAuthMiddleware: RequestHandlerMiddleware[Nothing, PersonRepo with PasswordEncoder, Nothing, Any] = {
    RequestHandlerMiddlewares.basicAuthZIO(cred => checkAuthentication(cred.uname, cred.upassword))
  }

  private def checkAuthentication(mailString: String, password: String): URIO[PersonRepo with PasswordEncoder, Boolean] = {
    val tryAuthenticate = for {
      repo <- ZIO.service[PersonRepo]
      encoder <- ZIO.service[PasswordEncoder]
      mail <- ZIO.succeed(new Email(mailString))
      person <- repo.findByEmail(mail)
    } yield checkProvidedPassword(password, encoder, person)
    tryAuthenticate <> ZIO.succeed(false)
  }

  private def checkProvidedPassword(password: String, encoder: PasswordEncoder, person: Option[Person]) = {
    person match {
      case Some(value) => encoder.validate(password, value.password)
      case None => false
    }
  }
}
