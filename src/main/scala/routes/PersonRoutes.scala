package routes

import controller.PersonServer
import domain._
import exception.CommonException
import service.PersonService
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir._
import sttp.tapir.{EndpointIO, Schema, endpoint, path}
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.{Task, ZIO, ZLayer}

import java.time.LocalDate

class PersonRoutes(val personService: PersonService) extends PersonServer {
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
  implicit val jsonCodec: JsonCodec[Person] = DeriveJsonCodec.gen
  implicit val schema: Schema[Person] = Schema.derived

  private val examplePerson = jsonBody[Person].example(person)
  private val examplePersonList = jsonBody[List[Person]].example(personList)

  private val exceptionOutput: EndpointIO.Body[String, CommonException] = jsonBody[CommonException].description("Common handled exception.")

  private val baseEndpoint = endpoint.in("api").in("v1").in("persons")
    .errorOut(exceptionOutput)
    .tags(List("Person Endpoints"))

  private val baseAuthEndpoint = baseEndpoint.securityIn(auth.basic[String]())
    .zServerSecurityLogic(token => ZIO.succeed("Viktor"))

  private val getPersons =
    baseAuthEndpoint.get
      .out(examplePersonList)
      .serverLogic(_ => _ => personService.getAllPersons)

  private val putPerson =
    baseAuthEndpoint.put
      .in(examplePerson)
      .out(examplePersonList)
      .serverLogic(_ => person => personService.updatePerson(List(person)))

  private val deletePerson =
    baseAuthEndpoint.delete
      .in(path[PersonId]("personId"))
      .serverLogic(_ => personId => personService.deletePersonWithId(personId))

  private val postPerson =
    baseEndpoint.post
      .in(examplePerson)
      .out(examplePersonList)
      .zServerLogic(person => personService.insertPerson(List(person)))

  private val allRoutes = ZioHttpInterpreter().toHttp(
    List(deletePerson, postPerson, putPerson, getPersons)
  )

  private val allEndpoints = List(putPerson, deletePerson, postPerson, getPersons)

  def httpRoutes =
    for {
      openApi <- ZIO.succeed(OpenAPIDocsInterpreter().serverEndpointsToOpenAPI(allEndpoints, "Duty app Server", "0.1"))
      openApiHttp <- ZIO.succeed(ZioHttpInterpreter().toHttp(SwaggerUI[Task](openApi.toYaml)))
    } yield openApiHttp
}

object PersonRoutes {
  def live: ZLayer[PersonService, Nothing, PersonServer] = ZLayer.fromFunction(
    (personService: PersonService) => new PersonRoutes(personService)
  )
}