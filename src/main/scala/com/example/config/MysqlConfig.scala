package com.example.config

import com.typesafe.config.ConfigFactory
import scala.util.Try

/**
 *
 * @author guobin.li@ascendex.io
 * @version 1.0,5/18/22
 */
case class MysqlConfig(url: String, user: String, password: String, driver: String)

object MysqlConfig {
  def load(): MysqlConfig = {
    val mysql = ConfigFactory.load().getConfig("mysql")
    MysqlConfig(
      mysql.getString("url"),
      mysql.getString("user"),
      Try(mysql.getString("password")).getOrElse(""),
      mysql.getString("driver")
    )
  }
}
