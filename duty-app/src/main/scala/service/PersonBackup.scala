package service

import repository.PersonRepo
import zio.json._
import zio.{Task, ZIO, ZLayer}

class PersonBackup(personRepo: PersonRepo, dataBackup: DataBackup) {
  def doBackup: Task[Unit] = for {
    allPersons <- personRepo.findAll()
    jsonPerson <- ZIO.attempt(allPersons.toJson)
    _ <- dataBackup.dump(jsonPerson)
  } yield ()
}

object PersonBackup {
  def live: ZLayer[PersonRepo, Nothing, PersonBackup] =
    ZLayer.fromFunction((repo: PersonRepo) => new PersonBackup(repo, new FileDataBackup()))
}
