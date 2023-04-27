package service

import auth.PasswordEncoder
import domain.{Person, PersonId}
import exception.CommonException
import exception.CommonException.AlreadyExistException
import repository.PersonRepo
import zio.{IO, Task, UIO, ZIO, ZLayer}

class PersonService(val personRepo: PersonRepo, val passEncoder: PasswordEncoder) {

  def getAllPersons: UIO[List[Person]] = personRepo.findAll()

  def deletePersonWithId(id: PersonId): UIO[Unit] = personRepo.deleteWithId(id)

  def updatePerson(persons: List[Person]): IO[CommonException,List[Person]] = for {
    _ <- checkMailAndTelephone(persons)
    encodedPass <- encodePassword(persons)
    updated <- ZIO.foreach(encodedPass)(personRepo.updatePerson)
  } yield updated

  def insertPerson(newPersons: List[Person]): IO [CommonException, List[Person]] = for {
    _ <- checkMailAndTelephone(newPersons)
    updatePerson <- generateId(newPersons) flatMap encodePassword
    inserted <- personRepo.insertAll(updatePerson)
  } yield inserted

  private def checkMailAndTelephone(newPersons: List[Person]): ZIO[Any, CommonException, Unit] = for {
    isEmailsExist <- personRepo.isPersonWithEmailExist(newPersons.map(_.mail)).orDie
    isTelephonesExist <- personRepo.isPersonWithSuchTelephonesExist(newPersons.map(_.telephone)).orDie
    _ <- ZIO.when(isEmailsExist)(ZIO.fail(AlreadyExistException("Mail is already registered in the system")))
    _ <- ZIO.when(isTelephonesExist)(ZIO.fail(AlreadyExistException("Telephone is already registered in the system")))
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