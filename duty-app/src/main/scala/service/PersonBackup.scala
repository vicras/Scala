package service

import repository.PersonRepo
import zio.json._
import zio.{Schedule, Task, UIO, ZIO, ZLayer, durationInt}

class PersonBackup(personRepo: PersonRepo, dataBackup: DataBackup) {

  private val backupRetryPolicy = (Schedule.recurs(10) && Schedule.fibonacci(1.second)) >>>
    Schedule.elapsed.map(time => ZIO.logInfo(s"Total time elapsed for dump: $time"))

  private val clearRetryPolicy = (Schedule.recurs(2) && Schedule.fibonacci(1.second)) >>>
    Schedule.elapsed.map(time => ZIO.logInfo(s"Total time elapsed for delete dump: $time"))

  def doBackup(): Task[Unit] = {
    for {
      allPersons <- personRepo.findAll()
      jsonPerson <- ZIO.attempt(allPersons.toJson)
      _ <- dataBackup.dump(jsonPerson)
    } yield ()
  }.retry(backupRetryPolicy)

  def clear: UIO[Unit] = dataBackup.clear.retryOrElse(clearRetryPolicy,
    (_, _: ZIO[Any, Nothing, Unit]) => ZIO.logError("Can't delete dump folder")).unit
}

object PersonBackup {
  def live: ZLayer[PersonRepo, Nothing, PersonBackup] =
    ZLayer.fromFunction((repo: PersonRepo) => new PersonBackup(repo, new FileDataBackup()))
}
