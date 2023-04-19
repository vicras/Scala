package layer

import auth.BasicAuthChecker
import controller.PersonServer
import controller.routes.PersonRoutes
import service.PersonService
import zio.ZLayer

object ControllerLayer {
  val controllers: ZLayer[PersonService with BasicAuthChecker, Nothing, PersonServer] = PersonRoutes.live
}
