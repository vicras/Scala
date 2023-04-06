package domain

import zio.json._

import java.util.UUID

final case class PersonId(value: String) extends AnyVal

object PersonId {
  def apply(): PersonId = {
    PersonId(UUID.randomUUID().toString)
  }

  implicit val personJsonEncoder: JsonEncoder[PersonId] = JsonEncoder[String].contramap(_.value)
  implicit val personJsonDecoder: JsonDecoder[PersonId] = JsonDecoder[String].map(apply)
}