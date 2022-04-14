package com.example.grpc

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.example.grpc.helloworld.GreeterServiceHandler

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GreeterServer {
//    val conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
//      .withFallback(ConfigFactory.defaultApplication())
//    val systemTypeded = ActorSystem[Nothing](Behaviors.empty, "GreeterServer", conf)
//
//    implicit val system = systemTypeded.classicSystem
//    implicit val ec: ExecutionContext = systemTypeded.executionContext

  def start(system1: ActorSystem[_]): Future[Http.ServerBinding] = {
    implicit val system = system1.classicSystem
    implicit val ec: ExecutionContext = system1.executionContext
    val service: HttpRequest => Future[HttpResponse] = {
      GreeterServiceHandler(new GreeterServiceImpl(system1))
    }
    val bound: Future[Http.ServerBinding] = Http().newServerAt("127.0.0.1", 8089)
      .bind(service)
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))
    bound.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        println("gRPC server bound to {}:{}", address.getHostString, address.getPort)
      case Failure(ex) =>
        println("Failed to bind gRPC endpoint, terminating system", ex)
        system.terminate()
    }
    bound
  }


}
