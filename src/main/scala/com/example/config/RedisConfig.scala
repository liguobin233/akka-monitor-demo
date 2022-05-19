package com.example.config

import redis.clients.jedis.Jedis
import com.typesafe.config.ConfigFactory

object RedisConfig {
  val jedisPooled: Jedis = initJedis()

  def initJedis(): Jedis = {
    val jedis = new Jedis(ConfigFactory.load().getString("redis.host"))
    jedis
  }
}
