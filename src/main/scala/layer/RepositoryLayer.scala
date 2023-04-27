package layer

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import repository.pg.PersonPGRepository
import zio.ZLayer

object RepositoryLayer {
  val repos: ZLayer[Quill.Postgres[SnakeCase], Nothing, PersonPGRepository] =
    PersonPGRepository.live
}
