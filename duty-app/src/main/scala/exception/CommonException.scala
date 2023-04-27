package exception

import sttp.tapir.Schema
import zio.json.{DeriveJsonCodec, JsonCodec}

sealed trait CommonException

object CommonException {
  case class AuthenticationException(text: String) extends CommonException
  case class InternalException(message: String) extends CommonException
  case class DocumentationException(message: String) extends CommonException
  case class AlreadyExistException(message: String) extends CommonException

  object AuthenticationException {
    implicit lazy val codec: JsonCodec[AuthenticationException] = DeriveJsonCodec.gen
    implicit lazy val schema: Schema[AuthenticationException] = Schema.derived
  }

  object DocumentationException {
    implicit lazy val codec: JsonCodec[DocumentationException] = DeriveJsonCodec.gen
    implicit lazy val schema: Schema[DocumentationException] = Schema.derived
  }

  object InternalException {
    implicit lazy val codec: JsonCodec[InternalException] = DeriveJsonCodec.gen
    implicit lazy val schema: Schema[InternalException] = Schema.derived
  }

  object AlreadyExistException {
    implicit lazy val codec: JsonCodec[AlreadyExistException] = DeriveJsonCodec.gen
    implicit lazy val schema: Schema[AlreadyExistException] = Schema.derived
  }

  implicit lazy val codec: JsonCodec[CommonException] = DeriveJsonCodec.gen
  implicit lazy val schema: Schema[CommonException] = Schema.derived
}
