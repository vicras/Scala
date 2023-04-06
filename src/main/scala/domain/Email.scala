package domain

import io.getquill.MappedEncoding
import org.apache.commons.lang3.StringUtils.EMPTY
import zio.json.{JsonDecoder, JsonEncoder}

sealed case class Email(address: String) {
}

object Email {

  def apply(address: String): Either[String, Email] = {
    Right(new Email(address))
  }

  implicit val encodeEmail: MappedEncoding[Email, String] = MappedEncoding[Email, String](_.address)
  implicit val decodeEmail: MappedEncoding[String, Email] = MappedEncoding[String, Email](num => apply(num).getOrElse(new Email(EMPTY)))

  implicit val personJsonEncoder: JsonEncoder[Email] = JsonEncoder[String].contramap(_.address)
  implicit val personJsonDecoder: JsonDecoder[Email] = JsonDecoder[String].map(new Email(_))
}
