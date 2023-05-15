package auth.spec

import auth.TestDataProvider.{email, person}
import auth.TestLayerProvider
import controller.PersonServer
import domain.dto.Person
import exception.ErrorHandler
import zio.http.model.{HeaderNames, Headers}
import zio.http.{Client, Server, ServerConfig}
import zio.json.DecoderOps
import zio.test.Assertion.equalTo
import zio.test.TestAspect.{retries, timed, timeout}
import zio.test.{Assertion, Spec, TestEnvironment, ZIOSpecDefault, assertZIO}
import zio.{Scope, ZIO, durationInt}

object End2EndSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("End-2-end tests suite")(
      test("Get persons success test") {
        val logicUnderTest: ZIO[PersonServer with Server with Client, Any, List[Person]] = for {
          apiRoutes <- ZIO.serviceWithZIO[PersonServer](_.httpRoutes)
          _ <- Server.install((apiRoutes).mapError(ErrorHandler.handle))
          login = email.address
          password = "admin"
          authHeader = Headers(HeaderNames.wwwAuthenticate, String.format("Bearer %s:%s", login, password))
          response <- Client.request("http://localhost:8080/api/v1/persons", headers = authHeader)
          personJson <- response.body.asString
          person <- ZIO.fromEither(personJson.fromJson[List[Person]])
        } yield person

        assertZIO(logicUnderTest)(Assertion.hasSize(equalTo(1))) *>
          assertZIO(logicUnderTest)(Assertion.hasSameElements(List(person)))
      }
    )
      .provide(TestLayerProvider.test,
        ServerConfig.live(ServerConfig.default.port(8080)),
        Server.live,
        Client.default,
      ) @@ timed @@ timeout(20.second) @@ retries(3)


}
