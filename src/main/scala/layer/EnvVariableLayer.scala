package layer

import com.typesafe.config.{Config, ConfigFactory}
import zio.ZLayer

object EnvVariableLayer {
  val configFactory: ZLayer[Any, Nothing, Config] = ZLayer.fromFunction(() => ConfigFactory.load())
}
