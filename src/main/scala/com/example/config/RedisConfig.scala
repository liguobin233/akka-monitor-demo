package com.example.config

import redis.clients.jedis.Jedis

object RedisConfig {
  val jedisPooled: Jedis = initJedis()

  def initJedis(): Jedis = {
    import redis.clients.jedis.Jedis
    val jedis = new Jedis("localhost")
    jedis
  }
}
