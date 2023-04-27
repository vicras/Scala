package repository

import domain.{Email, Person, PersonId, Telephone}
import zio._

trait PersonRepo {
  def updatePerson(person: Person): UIO[Person]

  def deleteWithId(id: PersonId): UIO[Unit]

  def insertAll(persons: Seq[Person]): UIO[List[Person]]

  def findAll(): UIO[List[Person]]

  def findByEmailAndTelephone(tel: Telephone, email: Email): Task[List[Person]]

  def findByEmail(email: Email): Task[Option[Person]]

  def isPersonWithSuchTelephonesExist(telephones: List[Telephone]): Task[Boolean]

  def isPersonWithEmailExist(telephones: List[Email]): Task[Boolean]
}


