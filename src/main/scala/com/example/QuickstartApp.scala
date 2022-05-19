package com.example

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.kafka.ProducerSettings
import com.example.grpc.GreeterServer
import com.typesafe.config.ConfigFactory
import kamon.Kamon
import org.apache.kafka.common.serialization.StringSerializer
import slick.jdbc.JdbcBackend.Database
import io.prometheus.client.hotspot.DefaultExports
import com.example.config.MysqlConfig

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success }

//#main-class
object QuickstartApp {
  //#start-http-server
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    // 需要使用0.0.0.0
    val futureBinding = Http().newServerAt("0.0.0.0", 8089).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }


  }

  Kamon.init()
  var db: Database = null

  //#start-http-server
  def main(args: Array[String]): Unit = {
    import akka.http.scaladsl.server.Directives._

    // prometheus client and provide a /metrics route
    DefaultExports.initialize()

    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      db = initDb
      val userRegistryActor = context.spawn(UserRegistry(), "UserRegistryActor")
      context.watch(userRegistryActor)

      val routes = new UserRoutes(userRegistryActor)(context.system)
      startHttpServer(routes.userRoutes ~ WebsocketRoute.route)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
    system.systemActorOf(
      Behaviors.setup[Done] { implicit ctx =>
        implicit val executionContext: ExecutionContext = ctx.executionContext
        GreeterServer.start(system) onComplete {
          case Failure(e) => println(s"grpc server exception caught:: $e")
          case Success(_) => println("binding grpc future completed")
        }
        Behaviors.receiveMessage { case Done => Behaviors.stopped }
      },
      "grpc-bind"
    )
    //#server-bootstrapping
  }

  def initDb(): Database = {
    val conf = MysqlConfig.load()
    val driver = conf.driver
    val parameters = Map(
      "cachePrepStmts" -> "true",
      "prepStmtCacheSize" -> "250",
      "prepStmtCacheSqlLimit" -> "2048",
      "useServerPrepStmts" -> "true",
      "useLocalSessionState" -> "true",
      "rewriteBatchedStatements" -> "true",
      "cacheResultSetMetadata" -> "true",
      "cacheServerConfiguration" -> "true",
      "elideSetAutoCommits" -> "true",
      "maintainTimeStats" -> "false",
      "allowPublicKeyRetrieval" -> "true", // to get around com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: Public Key Retrieval is not allowed
      "useSSL" -> "false", // @dev: this should only be used for development.
      "useUnicode" -> "yes", // MySQL table has to be set to charset of utf family as well
      "characterEncoding" -> "UTF-8" // e.g. "alter table <TB> convert to CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;
    ).map(a => s"${a._1}=${a._2}").mkString("&")
    val url = s"${conf.url}?$parameters"
    val configMap = Map[String, Any](
      "dataSourceClass" -> "slick.jdbc.DriverDataSource",
      "driverClassName" -> driver,
      "user" -> conf.user,
      "numThreads" -> 20,
      "properties.driver" -> driver,
      "properties.url" -> url,
      "connectionTimeout" -> 30000,
      "validationTimeout" -> 5000
    ) ++ (if (conf.password.nonEmpty) Map("password" -> conf.password) else Map.empty)
    Database.forConfig("", ConfigFactory.parseMap(collection.JavaConverters.mapAsJavaMap(configMap)))
  }

  def initKafka(system: ActorSystem[_]): Unit = {
    val config = system.settings.config.getConfig("akka.kafka.producer")
    val producerSettings =
      ProducerSettings(config, new StringSerializer, new StringSerializer)
        .withBootstrapServers("127.0.0.1:9002")
  }
}
//#main-class
