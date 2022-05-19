package com.example.grpc

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.example.grpc.helloworld.{GreeterServiceClient, HelloReply, HelloRequest}
import kamon.Kamon

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GreeterClient {

  def get(): Unit = {
    implicit val sys: ActorSystem[_] = ActorSystem(Behaviors.empty, "GreeterClient")
    implicit val ec: ExecutionContext = sys.executionContext


    val client = GreeterServiceClient(GrpcClientSettings.fromConfig("helloworld.GreeterService").withTls(false))

    val names = List("Alice", "Bob")

    names.foreach(singleRequestReply)

    def singleRequestReply(name: String): Unit = {
      println(s"Performing request: $name")
      val reply = client.sayHello(HelloRequest(name))
      reply.onComplete {
        case Success(msg) =>
          println(msg)
        case Failure(e) =>
          println(s"Error: $e")
      }
    }

    //#client-request-reply
    //#client-stream
    def streamingBroadcast(name: String): Unit = {
      println(s"Performing streaming requests: $name")

      val requestStream: Source[HelloRequest, NotUsed] =
        Source
          .tick(1.second, 1.second, "tick")
          .zipWithIndex
          .map { case (_, i) => i }
          .map(i => HelloRequest(s"$name-$i"))
          .mapMaterializedValue(_ => NotUsed)

      val responseStream: Source[HelloReply, NotUsed] = client.sayHelloToAll(requestStream)
      val done: Future[Done] =
        responseStream.runForeach(reply => println(s"$name got streaming reply: ${reply.message}"))

      done.onComplete {
        case Success(_) =>
          println("streamingBroadcast done")
        case Failure(e) =>
          println(s"Error streamingBroadcast: $e")
      }
    }
    //#client-stream
    //#client-request-reply

  }

  Kamon.init()

  def main(args: Array[String]): Unit = {
    get()
  }

}
