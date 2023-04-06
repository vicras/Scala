package layer

import controller.PersonApp
import service.PersonService
import zio.ZLayer

object ControllerLayer {
  val controllers: ZLayer[PersonService, Nothing, PersonApp] = PersonApp.live
}
