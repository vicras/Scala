package auth

import domain.Person
import exception.CommonException.AuthenticationException
import zio.test.Assertion.{anything, isSubtype}
import zio.test.{Assertion, Spec, TestEnvironment, ZIOSpecDefault, assertZIO}
import zio.{Scope, ZIO}

object BasicAuthCheckerTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Basic Auth Tests")(
      test("Success with right credentials") {
        val logicUnderTest: ZIO[BasicAuthChecker, AuthenticationException, Person] = for {
          checkResult <- ZIO.serviceWithZIO[BasicAuthChecker](_.basicAuthChecker("Basic dmljdG9yLmdyYXNrb3ZAZ21haWwuY29tOmFkbWlu"))
        } yield checkResult

        assertZIO(logicUnderTest)(Assertion.equalTo(TestDataProvider.person))
      },
      test("Failure with wrong credentials") {
        val logicUnderTest: ZIO[BasicAuthChecker, AuthenticationException, Person] = for {
          checkResult <- ZIO.serviceWithZIO[BasicAuthChecker](_.basicAuthChecker("Basic wrong_token"))
        } yield checkResult

        assertZIO(logicUnderTest.exit)(Assertion.fails(isSubtype[AuthenticationException](anything)))
      }
    )
      .provide(TestLayerProvider.fullAuthCheckLayer)
}
