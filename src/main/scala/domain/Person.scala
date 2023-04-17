package domain

import sttp.tapir.Schema
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate

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
  implicit val personJsonEncoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
  implicit val personJsonDecoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]

  implicit val schema: Schema[Person] = Schema.derived
}