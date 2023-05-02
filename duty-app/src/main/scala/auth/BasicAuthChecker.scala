package auth

import domain.dto.{Email, Person}
import exception.CommonException.AuthenticationException
import org.apache.commons.lang3.StringUtils.{EMPTY, SPACE}
import repository.PersonRepo
import zio.http.RequestHandlerMiddleware
import zio.http.middleware.RequestHandlerMiddlewares
import zio.{IO, UIO, ZIO, ZLayer}

import java.util.Base64

class BasicAuthChecker(personRepo: PersonRepo, passwordEncoder: PasswordEncoder) {
  val basicAuthMiddleware: RequestHandlerMiddleware[Nothing, Any, Nothing, Any] = {
    RequestHandlerMiddlewares.basicAuthZIO(cred => checkAuthenticationAndMapToBoolean(cred.uname, cred.upassword))
  }

  val basicAuthChecker: String => IO[AuthenticationException, Person] =
    token => (for {
      decodedToken <- ZIO.attempt(token.split(SPACE)
        .lastOption
        .map(base64String => new String(Base64.getDecoder.decode(base64String)))
        .getOrElse(EMPTY))
      userNameAndPass <- ZIO.attempt((decodedToken.split(":")(0), decodedToken.split(":")(1)))
      person <- checkAuthentication(userNameAndPass._1, userNameAndPass._2)
    } yield person) <> authenticationException

  private def checkAuthenticationAndMapToBoolean(mailString: String, password: String): UIO[Boolean] =
    checkAuthentication(mailString, password).foldZIO(
      failure => ZIO.succeed(false),
      success => ZIO.succeed(true)
    )

  private def checkAuthentication(mailString: String, password: String):
  IO[AuthenticationException, Person] =
    (for {
      mail <- ZIO.succeed(new Email(mailString))
      person <- personRepo.findByEmail(mail)
      validated <- if (checkProvidedPassword(password, passwordEncoder, person)) ZIO.succeed(person.get) else authenticationException
    } yield validated) <> authenticationException

  private def authenticationException = {
    ZIO.fail(new AuthenticationException("Can't authenticate user"))
  }

  private def checkProvidedPassword(password: String, encoder: PasswordEncoder, person: Option[Person]) = {
    person match {
      case Some(value) => encoder.validate(password, value.password)
      case None => false
    }
  }
}

object BasicAuthChecker {
  def live: ZLayer[PersonRepo with PasswordEncoder, Nothing, BasicAuthChecker] =
    ZLayer.fromFunction((personRepo: PersonRepo, passwordEncoder: PasswordEncoder) => new BasicAuthChecker(personRepo, passwordEncoder))
}
