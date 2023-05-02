package auth

import domain.dto.{Email, Person, PersonId, Telephone}
import repository.PersonRepo
import zio.{Task, UIO, ZIO, ZLayer}

object TestLayerProvider {
  def personLayer: ZLayer[Any, Nothing, PersonRepo] = ZLayer.fromZIO(personRepo)

  def passwordEncoder: ZLayer[Any, Nothing, PasswordEncoder] = BCryptPasswordEncoder.live

  def authChecker: ZLayer[PasswordEncoder with PersonRepo, Nothing, BasicAuthChecker] = ZLayer.fromZIO{
    for{
      personRepo <- ZIO.service[PersonRepo]
      passEncoder <- ZIO.service[PasswordEncoder]

    }yield new BasicAuthChecker(personRepo = personRepo, passwordEncoder = passEncoder)
  }

  def fullAuthCheckLayer: ZLayer[Any, Nothing, BasicAuthChecker] = (personLayer ++ passwordEncoder) >>> authChecker

  private def personRepo: ZIO[Any, Nothing, PersonRepo] = ZIO.succeed(new PersonRepo {
    def updatePerson(person: Person): UIO[Person] = ???

    def deleteWithId(id: PersonId): UIO[Unit] = ???

    def insertAll(persons: Seq[Person]): UIO[List[Person]] = ???

    def findAll(): UIO[List[Person]] = ???

    def findByEmailAndTelephone(tel: Telephone, email: Email): Task[List[Person]] = ???

    def findByEmail(email: Email): Task[Option[Person]] = ZIO.succeed(Some(TestDataProvider.person))

    def isPersonWithSuchTelephonesExist(telephones: List[Telephone]) = ???

    def isPersonWithEmailExist(telephones: List[Email]) = ???
  })
}
