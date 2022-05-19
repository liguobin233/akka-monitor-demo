package com.example.kafka

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object KafkaProducer {
  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem[Nothing](Behaviors.empty, "alpakka-samples")

  def produce(): Unit = {
    val producerSettings =
      ProducerSettings(actorSystem.classicSystem, new StringSerializer, new StringSerializer)
        .withBootstrapServers("127.0.0.1:9092")
    val done: Future[Done] =
      Source(1 to 10)
        .map(_.toString)
        .map(value => new ProducerRecord[String, String]("test", value))
        .runWith(Producer.plainSink(producerSettings))
    Await.result(done, Duration.Inf)
  }

}
