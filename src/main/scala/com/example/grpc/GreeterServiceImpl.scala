package com.example.grpc

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}
import com.example.dao.Logable
import com.example.grpc.helloworld.{GreeterService, HelloReply, HelloRequest}

import scala.concurrent.Future

class GreeterServiceImpl(system: ActorSystem[_]) extends GreeterService with Logable {
  private implicit val sys: ActorSystem[_] = system
  //#service-request-reply
  val (inboundHub: Sink[HelloRequest, NotUsed], outboundHub: Source[HelloReply, NotUsed]) =
    MergeHub.source[HelloRequest]
      .map(request => HelloReply(s"Hello, ${request.name}"))
      .toMat(BroadcastHub.sink[HelloReply])(Keep.both)
      .run()
  //#service-request-reply

  /**
   * Sends a greeting
   */
  override def sayHello(in: HelloRequest): Future[HelloReply] = {
    logger.info("grpc server test log.............")
    Future.successful(HelloReply(s"Hello,${in.name}"))
  }

  /**
   * #service-request-reply
   * #service-stream
   * The stream of incoming HelloRequest messages are
   * sent out as corresponding HelloReply. From
   * all clients to all clients, like a chat room.
   */
  override def sayHelloToAll(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = {
    in.runWith(inboundHub)
    outboundHub
  }
}
