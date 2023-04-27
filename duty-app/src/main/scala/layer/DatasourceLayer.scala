package layer

import com.typesafe.config.Config
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import zio.ZLayer

import javax.sql.DataSource

object DatasourceLayer {
  val hikariConfig: ZLayer[Config, Nothing, HikariConfig] = ZLayer.fromFunction(getHikariConfig _)
  val hikariDataSource: ZLayer[HikariConfig, Nothing, DataSource] = ZLayer.fromFunction((conf: HikariConfig) => new HikariDataSource(conf))

  private def getHikariConfig(conf: Config): HikariConfig = {
    val config = new HikariConfig()
    config.setDriverClassName(conf.getString("duty-app.database.datasource.classname"))
    config.setJdbcUrl(conf.getString("duty-app.database.datasource.url"))
    config.setUsername(conf.getString("duty-app.database.datasource.user"))
    config.setPassword(conf.getString("duty-app.database.datasource.password"))
    config
  }
}
