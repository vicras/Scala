package auth

import controller.PersonServer
import domain.dto.{Email, Person, PersonId, Telephone}
import layer.{ControllerLayer, EnvVariableLayer, ServiceLayer}
import repository.PersonRepo
import zio.{Task, UIO, ZIO, ZLayer}

object TestLayerProvider {
  def personRepoLayer: ZLayer[Any, Nothing, PersonRepo] = ZLayer.fromZIO(personRepo)

  def passwordEncoder: ZLayer[Any, Nothing, PasswordEncoder] = BCryptPasswordEncoder.live

  def authChecker: ZLayer[PasswordEncoder with PersonRepo, Nothing, BasicAuthChecker] = ZLayer.fromZIO {
    for {
      personRepo <- ZIO.service[PersonRepo]
      passEncoder <- ZIO.service[PasswordEncoder]

    } yield new BasicAuthChecker(personRepo = personRepo, passwordEncoder = passEncoder)
  }

  def fullAuthCheckLayer: ZLayer[Any, Nothing, BasicAuthChecker] = (personRepoLayer ++ passwordEncoder) >>> authChecker

  val test: ZLayer[Any, Nothing, PersonServer] =
    ZLayer.make[PersonServer](
      passwordEncoder,
      EnvVariableLayer.configFactory,
      ServiceLayer.services,
      ControllerLayer.controllers,
      personRepoLayer,
      fullAuthCheckLayer
    )

  private def personRepo: ZIO[Any, Nothing, PersonRepo] = ZIO.succeed(new PersonRepo {
    def updatePerson(person: Person): UIO[Person] = ???

    def deleteWithId(id: PersonId): UIO[Unit] = ???

    def insertAll(persons: Seq[Person]): UIO[List[Person]] = ???

    def findAll(): UIO[List[Person]] = ZIO.succeed(List(TestDataProvider.person))

    def findByEmailAndTelephone(tel: Telephone, email: Email): Task[List[Person]] = ???

    def findByEmail(email: Email): Task[Option[Person]] = ZIO.succeed(Some(TestDataProvider.person))

    def isPersonWithSuchTelephonesExist(telephones: List[Telephone]) = ???

    def isPersonWithEmailExist(telephones: List[Email]) = ???
  })
}
