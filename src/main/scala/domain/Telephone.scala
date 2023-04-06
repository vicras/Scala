package domain

import io.getquill.MappedEncoding
import zio.json.{JsonDecoder, JsonEncoder}

//+ 375 29 747 33 31
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
}
