package com.example.dao

import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

case class BotCommand(id: Long = 0, command: String, content: String, tips: String, status: Int, parseType: Int)

object BotCommand {
  implicit def convertor(tuple6: (Long, String, String, String, Int, Int)): BotCommand = {
    BotCommand(tuple6._1, tuple6._2, tuple6._3, tuple6._4, tuple6._5, tuple6._6)
  }
}

class TelegramBotCommand(tag: Tag)
  extends Table[(Long, String, String, String, Int, Int)](tag, "t_telegram_bot_command") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey)

  def command: Rep[String] = column[String]("command")

  def content: Rep[String] = column[String]("content")

  def tips: Rep[String] = column[String]("tips")

  def status: Rep[Int] = column[Int]("status")

  def parseType: Rep[Int] = column[Int]("parse_type")

  def * : ProvenShape[(Long, String, String, String, Int, Int)] =
    (id, command, content, tips, status, parseType)
}
