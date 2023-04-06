package domain

import io.getquill.MappedEncoding
import zio.json.{JsonDecoder, JsonEncoder}

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
}
