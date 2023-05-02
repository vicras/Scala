package auth

import domain.dto.{Email, Language, Person, PersonId, Telephone}

import java.time.LocalDate

object TestDataProvider {
  val personId: PersonId = PersonId()
  val telephone: Telephone = Telephone(375297473331L).toOption.get
  val email: Email = Email("victor.graskov@gmail.com").toOption.get

  val person = new Person(
    id = personId,
    firstName = "Viktar",
    lastName = "Hraskou",
    patronymic = "Ivanovich",
    telegramId = 123456789,
    birthDate = LocalDate.of(2000, 11, 30),
    telephone = telephone,
    password = "$2a$10$2BRocOrprVeknMUK1ixgceux8KwXdVpyaqYmvPAeRbO/TZu3NazmS", // base64 "admin"
    mail = email,
    homeAddress = "Address",
    language = Language.ENG
  )
}
