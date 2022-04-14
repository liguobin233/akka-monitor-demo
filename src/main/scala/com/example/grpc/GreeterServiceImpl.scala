package com.example.grpc

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}
import com.example.OkHttpInterceptor
import com.example.dao.Logable
import com.example.grpc.helloworld.{GreeterService, HelloReply, HelloRequest}
import okhttp3.{Request, Response}

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
    http2()
    Future.successful(HelloReply(s"Hello,${in.name}"))
  }

  import okhttp3.OkHttpClient

  val client = new OkHttpClient
  .Builder()
    //    .protocols(util.Arrays.asList(Protocol.H2_PRIOR_KNOWLEDGE))
    .addInterceptor(new OkHttpInterceptor).build()

  def http2(): String = {

    val request: Request = new Request.Builder().url("https://www.baidu.com/").build()
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
