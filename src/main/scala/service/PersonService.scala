package service

import auth.PasswordEncoder
import domain.{Person, PersonId}
import exception.DutyAppException
import repository.PersonRepo
import zio.{IO, Task, UIO, ZIO, ZLayer}

class PersonService(val personRepo: PersonRepo, val passEncoder: PasswordEncoder) {

  def getAllPersons: Task[List[Person]] = personRepo.findAll()

  def deletePersonWithId(id: PersonId): Task[Unit] = personRepo.deleteWithId(id)

  def updatePerson(persons: List[Person]): Task[List[Person]] = for {
    _ <- checkMailAndTelephone(persons)
    encodedPass <- encodePassword(persons)
    updated <- ZIO.foreach(encodedPass)(personRepo.updatePerson)
  } yield updated

  def insertPerson(newPersons: List[Person]): IO[Throwable, List[Person]] = for {
    _ <- checkMailAndTelephone(newPersons)
    updatePerson <- generateId(newPersons) flatMap encodePassword
    inserted <- personRepo.insertAll(updatePerson)
  } yield inserted

  private def checkMailAndTelephone(newPersons: List[Person]): ZIO[Any, DutyAppException, Unit] = for {
    isEmailsExist <- personRepo.isPersonWithEmailExist(newPersons.map(_.mail)).orDie
    isTelephonesExist <- personRepo.isPersonWithSuchTelephonesExist(newPersons.map(_.telephone)).orDie
    _ <- ZIO.when(isEmailsExist)(ZIO.fail(DutyAppException("Mail is already registered in the system")))
    _ <- ZIO.when(isTelephonesExist)(ZIO.fail(DutyAppException("Telephone is already registered in the system")))
  } yield ()

  private def generateId(newPersons: List[Person]): UIO[List[Person]] = for {
    updatedPersons <- ZIO.foreach(newPersons)({ per =>
      ZIO.succeed(per.copy(id = PersonId()))
    })
  } yield updatedPersons

  private def encodePassword(newPersons: List[Person]): UIO[List[Person]] = for {
    updatedPersons <- ZIO.foreach(newPersons)({ per =>
      ZIO.succeed(per.copy(password = passEncoder.encode(per.password)))
    })
  } yield updatedPersons
}

object PersonService {
  def live: ZLayer[PersonRepo with PasswordEncoder, Nothing, PersonService] =
    ZLayer.fromFunction((repo: PersonRepo, encoder: PasswordEncoder) => new PersonService(repo, encoder))
}