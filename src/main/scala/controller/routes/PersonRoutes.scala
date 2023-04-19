package controller.routes

import controller.PersonServer
import domain._
import exception.CommonException
import service.PersonService
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir._
import _root_.auth.BasicAuthChecker
import sttp.tapir.{EndpointIO, endpoint, path}
import zio.http.HttpApp
import zio.{ZIO, ZLayer}

import java.time.LocalDate

class PersonRoutes(val personService: PersonService, authChecker: BasicAuthChecker) extends PersonServer {
  private val personId: PersonId = PersonId()
  private val telephone: Telephone = Telephone(375297473331L).toOption.get
  private val email: Email = Email("victor.graskov@gmail.com").toOption.get
  private val person = new Person(
    id = personId,
    firstName = "Viktar",
    lastName = "Hraskou",
    patronymic = "Ivanovich",
    telegramId = 123456789,
    birthDate = LocalDate.of(2000, 11, 30),
    telephone = telephone,
    password = "$2a$10$sY7InGIGXBmxkvyj93iOaeY8pOMqZ3XIU55f7jSzOEuRaXAFF4472", // base64 "admin"
    mail = email,
    homeAddress = "Address",
    language = Language.ENG
  )
  private val personList = List(person)

  private val examplePerson = jsonBody[Person].example(person)
  private val examplePersonList = jsonBody[List[Person]].example(personList)

  private val exceptionOutput: EndpointIO.Body[String, CommonException] = jsonBody[CommonException].description("Common handled exception.")

  private val baseEndpoint = endpoint.in("api").in("v1").in("persons")
    .errorOut(exceptionOutput)
    .tags(List("Person Endpoints"))

  private val baseAuthEndpoint = baseEndpoint.securityIn(auth.basic[String]())
    .zServerSecurityLogic(token => authChecker.basicAuthChecker(token))

  private val getPersons =
    baseAuthEndpoint.get
      .description("Get all users from Database")
      .out(examplePersonList)
      .serverLogic(_ => _ => personService.getAllPersons)

  private val putPerson =
    baseAuthEndpoint.put
      .description("Update existing user")
      .in(examplePerson)
      .out(examplePersonList)
      .serverLogic(_ => person => personService.updatePerson(List(person)))

  private val deletePerson =
    baseAuthEndpoint.delete
      .description("Delete person by id")
      .in(path[PersonId]("personId"))
      .serverLogic(_ => personId => personService.deletePersonWithId(personId))

  private val postPerson =
    baseEndpoint.post
      .description("Add new person to database")
      .in(examplePerson)
      .out(examplePersonList)
      .zServerLogic(person => personService.insertPerson(List(person)))

  private val allRoutes = ZioHttpInterpreter().toHttp(
    List(deletePerson, postPerson, putPerson, getPersons)
  )

  private val allEndpoints: List[ZServerEndpoint[Any, Nothing]] = List(putPerson, deletePerson, postPerson, getPersons)

  def httpRoutes: ZIO[Any, Nothing, HttpApp[Any, Throwable]] = ZIO.succeed(allRoutes)

  override def endpoints: ZIO[Any, Nothing, List[ZServerEndpoint[Any, Nothing]]] = ZIO.succeed(allEndpoints)
}

object PersonRoutes {
  def live: ZLayer[PersonService with BasicAuthChecker, Nothing, PersonServer] = ZLayer.fromFunction(
    (personService: PersonService, authChecker: BasicAuthChecker) => new PersonRoutes(personService, authChecker)
  )
}