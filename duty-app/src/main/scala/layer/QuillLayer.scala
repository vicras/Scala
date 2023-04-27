package layer

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.ZLayer

import javax.sql.DataSource

object QuillLayer {
  val quillPostgresCtx: ZLayer[DataSource, Nothing, Quill.Postgres[SnakeCase.type]] = Quill.Postgres.fromNamingStrategy(SnakeCase)
}
