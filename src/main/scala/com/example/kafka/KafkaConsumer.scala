package com.example.kafka

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import kamon.Kamon
import kamon.instrumentation.kafka.client.KafkaInstrumentation.runWithConsumerSpan
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.DurationInt

object KafkaConsumer extends App {
  Kamon.init()
  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "KafkaToElasticSearch")
  implicit val executionContext: ExecutionContext = actorSystem.executionContext

  val topic = "test"
  private val groupId = "demo"

  val kafkaConsumerSettings = ConsumerSettings(actorSystem.classicSystem, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("127.0.0.1:9092")
    .withGroupId(groupId)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    .withStopTimeout(0.seconds)

  consume()

  def consume(): Unit = {
    val control: Consumer.DrainingControl[Done] = Consumer
      .sourceWithOffsetContext(kafkaConsumerSettings, Subscriptions.topics(topic)) // (5)
      .map { consumerRecord => // (6)
        runWithConsumerSpan(consumerRecord) {
          val movie = consumerRecord.value()
          println(movie)
        }
      }
      .via(Committer.flowWithOffsetContext(CommitterSettings(actorSystem.classicSystem))) // (9)
      .toMat(Sink.ignore)(Consumer.DrainingControl.apply) // (10)
      .run()
    Thread.sleep(5.seconds.toMillis)
    val copyingFinished = control.drainAndShutdown()
    Await.result(copyingFinished, 10.seconds)
  }
}
