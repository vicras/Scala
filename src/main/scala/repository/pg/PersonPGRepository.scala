package repository.pg

import domain.{Email, Person, PersonId, Telephone}
import io.getquill.jdbczio.Quill
import io.getquill.{EntityQuery, Quoted, SnakeCase}
import repository.PersonRepo
import zio._

class PersonPGRepository(ctx: Quill.Postgres[SnakeCase]) extends PersonRepo {

  import ctx._

  private val people: Quoted[EntityQuery[Person]] = quote(query[Person])

  override def insertAll(persons: Seq[Person]): Task[List[Person]] = for {
    peoples <- run(quote(liftQuery(persons).foreach(e => query[Person].insertValue(e).returning(a => a))))
  } yield peoples

  override def findAll(): Task[List[Person]] = for {
    peoples <- ctx.run(people)
  } yield peoples

  override def findByEmailAndTelephone(tel: Telephone, email: Email): Task[List[Person]] = for {
    peoples <- ctx.run(people.filter(per => per.mail == lift(email) && per.telephone == lift(tel)))
  } yield peoples

  override def isPersonWithSuchTelephonesExist(telephones: List[Telephone]): Task[Boolean] = for {
    isExist <- ctx.run(!people.filter(per => liftQuery(telephones).contains(per.telephone)).isEmpty)
  } yield isExist

  override def isPersonWithEmailExist(emails: List[Email]): Task[Boolean] = for {
    isExist <- ctx.run(!people.filter(per => liftQuery(emails).contains(per.mail)).isEmpty)
  } yield isExist

  override def deleteWithId(id: PersonId): Task[Unit] = {
    for {
      _ <- ctx.run(people.filter(per => per.id == lift((id))).delete)
    } yield ()
  }

  override def updatePerson(person: Person): Task[Person] = for {
    _ <- ctx.run(people.filter(per => per.id == lift(person.id)).updateValue(lift(person)))
  } yield person

  override def findByEmail(email: Email): Task[Option[Person]] = for {
    peoples <- ctx.run(people.filter(per => per.mail == lift(email)))
  } yield peoples.headOption
}

object PersonPGRepository {
  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, PersonPGRepository] = ZLayer.fromFunction(create _)

  private def create(ctx: Quill.Postgres[SnakeCase]): PersonPGRepository = new PersonPGRepository(ctx)
}
