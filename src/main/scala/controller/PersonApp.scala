package controller

import _root_.service.PersonService
import auth.{BasicAuthMiddleware, PasswordEncoder}
import domain.{Person, PersonId}
import exception.{DutyAppException, ErrorHandler}
import repository.PersonRepo
import zhttp.http._
import zio.json._
import zio.{ZIO, ZLayer}

class PersonApp(val service: PersonService) {

  val app: HttpApp[PersonRepo with PasswordEncoder, Throwable] = combineEndpoints.catchAll(ErrorHandler.handle)

  private def combineEndpoints = {
    commonEndpoints ++ (securityRequiredEndpoints @@ BasicAuthMiddleware.basicAuthMiddleware)
  }

  private def commonEndpoints: HttpApp[Any, Throwable] = Http.collectZIO[Request] {
    case req@Method.POST -> !! / "api" / "v1" / "persons" => for {
      body <- req.body.asString
      person <- ZIO.fromEither(body.fromJson[Person]).catchAll(info => ZIO.fail(DutyAppException(info)))
      persons <- service.insertPerson(List(person))
      resp <- ZIO.succeed(Response.text(persons.toJson))
    } yield resp
  }

  private def securityRequiredEndpoints: HttpApp[Any, Throwable] = Http.collectZIO[Request] {
    case Method.GET -> !! / "api" / "v1" / "persons" => for {
      persons <- service.getAllPersons.orDie
      resp <- ZIO.succeed(Response.json(persons.toJson))
    } yield resp

    case Method.DELETE -> !! / "api" / "v1" / "persons" / id => for {
      _ <- service.deletePersonWithId(PersonId(id))
      resp <- ZIO.succeed(Response.ok)
    } yield resp

    case req@Method.PUT -> !! / "api" / "v1" / "persons" => for {
      body <- req.body.asString
      person <- ZIO.fromEither(body.fromJson[Person]).catchAll(info => ZIO.fail(DutyAppException(info)))
      persons <- service.updatePerson(List(person))
      resp <- ZIO.succeed(Response.text(persons.toJson))
    } yield resp
  }
}

object PersonApp {
  val live: ZLayer[PersonService, Nothing, PersonApp] =
    ZLayer.fromFunction((service: PersonService) => new PersonApp(service))
}
