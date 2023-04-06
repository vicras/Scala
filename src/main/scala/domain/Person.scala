package domain

import io.getquill.MappedEncoding
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate
import java.util.UUID

final case class Person(
                         id: PersonId,
                         firstName: String,
                         lastName: String,
                         patronymic: String,
                         telegramId: Int,
                         birthDate: LocalDate,
                         telephone: Telephone,
                         password: String,
                         mail: Email,
                         homeAddress: String,
                         language: Language
                       ) {}

object Person {
  implicit val encodeUUID: MappedEncoding[UUID, String] = MappedEncoding[UUID, String](_.toString)
  implicit val decodeUUID: MappedEncoding[String, UUID] = MappedEncoding[String, UUID](UUID.fromString)

  implicit val personJsonEncoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
  implicit val personJsonDecoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]
}