package domain

import io.getquill.MappedEncoding
import org.apache.commons.lang3.StringUtils.EMPTY
import sttp.tapir.Schema
import zio.json.{DeriveJsonCodec, JsonCodec, JsonDecoder, JsonEncoder}

import java.time.LocalDate
import java.util.UUID



object dto {
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

    implicit val schema: Schema[Email] = Schema.derived
  }

  sealed trait Language

  object Language {
    val RU: Language = new Language {
      override def toString: String = "RU"
    }
    val ENG: Language = new Language {
      override def toString: String = "ENG"
    }

    private def fromString(name: String): Option[Language] = name match {
      case lang if lang.equalsIgnoreCase("RU") => Some(RU)
      case lang if lang.equalsIgnoreCase("ENG") => Some(ENG)
      case _ => None
    }

    implicit val encodeLanguage: MappedEncoding[Language, String] = MappedEncoding[Language, String](_.toString)
    implicit val decodeLanguage: MappedEncoding[String, Language] = MappedEncoding[String, Language](fromString(_).get)

    implicit val personJsonEncoder: JsonEncoder[Language] = JsonEncoder[String].contramap(_.toString)
    implicit val personJsonDecoder: JsonDecoder[Language] = JsonDecoder[String].map(fromString(_).get)

    implicit val schema: Schema[Language] = Schema.string
  }

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
    implicit val personJsonCodec: JsonCodec[Person] = DeriveJsonCodec.gen[Person]
    //  implicit val personJsonDecoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]

    implicit val schema: Schema[Person] = Schema.derived
  }

  final case class PersonId(value: String) extends AnyVal

  object PersonId {
    def apply(): PersonId = {
      PersonId(UUID.randomUUID().toString)
    }

    implicit val personJsonEncoder: JsonEncoder[PersonId] = JsonEncoder[String].contramap(_.value)
    implicit val personJsonDecoder: JsonDecoder[PersonId] = JsonDecoder[String].map(apply)

    implicit val schema: Schema[PersonId] = Schema.derived
  }

  sealed case class Telephone(number: Long) {
    override def toString() = {
      val countryPrefix = number.toString.substring(0, 3)
      val numberPrefix = number.toString.substring(3, 5)
      val firstNumberPart = number.toString.substring(5, 8)
      val secondNumberPart = number.toString.substring(8, 10)
      val thirdNumberPart = number.toString.substring(10, 12)
      s"+ $countryPrefix $numberPrefix $firstNumberPart $secondNumberPart $thirdNumberPart"
    }
  }

  object Telephone {
    private val BELARUSIAN_TELEPHONE_NUMBER_LENGTH = 12
    val WRONG_TELEPHONE = new Telephone(0)

    def apply(number: Long): Either[String, Telephone] = {
      if (number.toString.length == BELARUSIAN_TELEPHONE_NUMBER_LENGTH) Right(new Telephone(number))
      else Left("Wrong number format, Belarus mobile number contains 12 digits")
    }

    implicit val encodeTelephone: MappedEncoding[Telephone, Long] = MappedEncoding[Telephone, Long](_.number)
    implicit val decodeTelephone: MappedEncoding[Long, Telephone] = MappedEncoding[Long, Telephone](num => apply(num).getOrElse(WRONG_TELEPHONE))

    implicit val personJsonEncoder: JsonEncoder[Telephone] = JsonEncoder[Long].contramap(_.number)
    implicit val personJsonDecoder: JsonDecoder[Telephone] = JsonDecoder[Long].mapOrFail(apply)

    implicit val schema: Schema[Telephone] = Schema.derived
  }


}