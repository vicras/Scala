package auth

import domain.{Email, Person}
import repository.PersonRepo
import zhttp.http.Middleware
import zhttp.http.middleware.HttpMiddleware
import zio.{URIO, ZIO}

object BasicAuthMiddleware {
  val basicAuthMiddleware: HttpMiddleware[PersonRepo with PasswordEncoder, Nothing] = {
    Middleware.basicAuthZIO(cred => checkAuthentication(cred.uname, cred.upassword))
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
