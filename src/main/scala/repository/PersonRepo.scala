package repository

import domain.{Email, Person, PersonId, Telephone}
import zio._

trait PersonRepo {
  def updatePerson(person: Person): Task[Person]

  def deleteWithId(id: PersonId): Task[Unit]

  def insertAll(persons: Seq[Person]): Task[List[Person]]

  def findAll(): Task[List[Person]]

  def findByEmailAndTelephone(tel: Telephone, email: Email): Task[List[Person]]

  def findByEmail(email: Email): Task[Option[Person]]

  def isPersonWithSuchTelephonesExist(telephones: List[Telephone]): Task[Boolean]

  def isPersonWithEmailExist(telephones: List[Email]): Task[Boolean]
}


