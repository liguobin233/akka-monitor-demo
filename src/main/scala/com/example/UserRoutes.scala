package com.example

import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.example.UserRegistry._
import kamon.Kamon
import kamon.context.Context
import okhttp3.{ Protocol, Request, Response }
import sttp.client3
import sttp.client3.Identity
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.client3.quick._
import sttp.client3.sprayJson.asJson
import okhttp3.OkHttpClient

import java.util
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success }
import sttp.client3.ResponseException

//#import-json-formats
//#user-routes-class
class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class

  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getUsers(): Future[Users] = {
    system.log.info("log test 4 traceId and spanId")
    1 to 20000 foreach {
      i:Int =>
        userRegistry.ask(GetUsers)  
    }
    userRegistry.ask(GetUsers)
  }

  def getUser(name: String): Future[GetUserResponse] =
    userRegistry.ask(GetUser(name, _))


  def createUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(CreateUser(user, _))

  def deleteUser(name: String): Future[ActionPerformed] =
    userRegistry.ask(DeleteUser(name, _))

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val userRoutes: Route =
  pathPrefix("users2") {
    concat(
      //#users-get-delete
      pathEnd {
        concat(
          get {
            //trace日志
            system.log.info("log test 4 traceId and spanId")
            //http请求
            //              system.log.info(s"http2:${http2()}")
            //mysql
            system.log.info("mysql")
            Test.mysql()
            //akka actor
            val result = complete(getUsers())
            // akka http client
            http()

            // sttp
            sttpClient()
            asyncSttpClient()
            val span = Kamon.currentSpan()
            println(s"kamon currentSpan => ${span.id}")
            result
          },
          post {
            entity(as[User]) { user =>
              onSuccess(createUser(user)) { performed =>
                complete((StatusCodes.Created, performed))
              }
            }
          })
      },
      //#users-get-delete
      //#users-get-post
      path(Segment) { name =>
        concat(
          get {
            //#retrieve-user-info
            rejectEmptyResponse {
              onSuccess(getUser(name)) { response =>
                complete(response.maybeUser)
              }
            }
            //#retrieve-user-info
          },
          delete {
            //#users-delete-logic
            onSuccess(deleteUser(name)) { performed =>
              complete((StatusCodes.OK, performed))
            }
            //#users-delete-logic
          })
      })
    //#users-get-delete
  }

  def http(): Unit = {
//    Http().connectionTo("127.0.0.1").toPort(8081).http2()
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://httpbin.org/ip"))
    implicit val executionContext = system.executionContext
    responseFuture
      .onComplete {
        case Success(res) => println(res)
        case Failure(_) => sys.error("something wrong")
      }
  }

  val client = new OkHttpClient
  .Builder()
    .protocols(util.Arrays.asList(Protocol.H2_PRIOR_KNOWLEDGE))
    .addInterceptor(new OkHttpInterceptor).build()

  def http2(): String = {
    val request: Request = new Request.Builder().url("http://127.0.0.1:8088/users2").build()
    val response: Response = null
    try {
      val response = client.newCall(request).execute
      response.body.string
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        ""
    } finally {
      if (response != null) response.close()
    }
  }

  def asyncSttpClient(): Unit = {
    val backend = AsyncHttpClientFutureBackend()
    val future: Future[client3.Response[Either[String, String]]] = basicRequest.get(uri"https://www.baidu.com").send(backend)
    val ret = Await.result(future, Duration.Inf)
    println(ret)

  }

  def sttpClient(): Unit = {
    val s1: Identity[client3.Response[String]] = basicRequest.get(uri"https://github.com/")
      .response(asStringAlways).send(backend)
    println(s1.body)
  }

  //#all-routes
}
