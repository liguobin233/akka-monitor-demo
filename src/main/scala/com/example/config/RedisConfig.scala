package com.example.config

object RedisConfig {

  import redis.clients.jedis.JedisPooled

  val jedisPooled: JedisPooled = initJedis()

  def initJedis(): JedisPooled = {
    val jedis = new JedisPooled("localhost", 6379)
    jedis
  }
}
