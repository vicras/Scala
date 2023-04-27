package layer

import auth.{BCryptPasswordEncoder, PasswordEncoder}
import repository.PersonRepo
import service.PersonService
import zio.ZLayer

object ServiceLayer {

  val passwordEncoder: ZLayer[Any, Nothing, PasswordEncoder] =
    BCryptPasswordEncoder.live

  val services: ZLayer[PasswordEncoder with PersonRepo, Nothing, PersonService] =
    PersonService.live
}
