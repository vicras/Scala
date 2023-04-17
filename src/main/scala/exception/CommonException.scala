package exception

import sttp.tapir.Schema
import zio.json.{DeriveJsonCodec, JsonCodec}

case class CommonException(string: String) extends RuntimeException {
}

object CommonException {
  implicit lazy val codec: JsonCodec[CommonException] = DeriveJsonCodec.gen
  implicit lazy val schema: Schema[CommonException] = Schema.derived
}
