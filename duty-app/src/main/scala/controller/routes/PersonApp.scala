package controller.routes

import _root_.service.PersonService
import domain.dto.{Person, PersonId}
import exception.CommonException.InternalException
import exception.{CommonException, ErrorHandler}
import zio.http._
import zio.http.model.Method
import zio.json._
import zio.{ZIO, ZLayer}

/**
 * Class example how to do the same with ZIO
 */
class PersonApp(val service: PersonService) {

  val app: App[Any] = combineEndpoints.mapError(ErrorHandler.handle)

  private def combineEndpoints = {
    commonEndpoints ++ (securityRequiredEndpoints)
  }

  private def commonEndpoints: HttpApp[Any, CommonException] = (Http.collectZIO[Request] {
    case req@Method.POST -> !! / "api" / "v1" / "persons" => for {
      body <- req.body.asString
      person <- ZIO.fromEither(body.fromJson[Person])
      persons <- service.insertPerson(List(person))
      resp <- ZIO.succeed(Response.text(persons.toJson))
    } yield resp
  }).mapError(info => InternalException(info.toString))

  private def securityRequiredEndpoints: HttpApp[Any, CommonException] = Http.collectZIO[Request] {
    case Method.GET -> !! / "api" / "v1" / "persons" => for {
      persons <- service.getAllPersons
      resp <- ZIO.succeed(Response.json(persons.toJson))
    } yield resp

    case Method.DELETE -> !! / "api" / "v1" / "persons" / id => for {
      _ <- service.deletePersonWithId(PersonId(id))
      resp <- ZIO.succeed(Response.ok)
    } yield resp

    case req@Method.PUT -> !! / "api" / "v1" / "persons" => for {
      body <- req.body.asString
      person <- ZIO.fromEither(body.fromJson[Person]).catchAll(info => ZIO.fail(new RuntimeException(info))).orDie
      persons <- service.updatePerson(List(person))
      resp <- ZIO.succeed(Response.text(persons.toJson))
    } yield resp
  }.mapError(info => InternalException(info.toString))
}

object PersonApp {
  val live: ZLayer[PersonService, Nothing, PersonApp] =
    ZLayer.fromFunction((service: PersonService) => new PersonApp(service))
}
