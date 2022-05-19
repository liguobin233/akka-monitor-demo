package com.example.dao
//import slick.jdbc.MySQLProfile.api._

object BotCommandDao extends Logable {



//  println("BotCommandDao 初始化")
  //  val db = QuickstartApp.db
  //  val commands: TableQuery[TelegramBotCommand] = TableQuery[TelegramBotCommand]
  //
  //  /**
  //   * 添加
  //   *
  //   * @param command
  //   */
  //  def addCommand(command: BotCommand): Future[Done] = {
  //    val insertActions = DBIO.seq(
  //      commands += (command.id, command.command, command.content, command.tips, command.status, command.parseType),
  //    )
  //    val insertResult = db.run(insertActions)
  //    Await.result(insertResult, 10.seconds)
  //    Future.successful(Done)
  //  }
  //
  //  /**
  //   * 根据ID查询
  //   *
  //   * @param id
  //   * @return
  //   */
  //  def queryById(id: Long): Option[BotCommand] = {
  //    val action = commands.filter(_.id === id).result.headOption
  //    val result = Await.result(db.run(action), 10.seconds)
  //    result match {
  //      case Some(tuple) => Option(tuple)
  //      case _ => None
  //    }
  //  }
  //
  //  /**
  //   * 修改
  //   *
  //   * @param command
  //   */
  //  def updateCommand(command: BotCommand): Future[Done] = {
  //    val updateAction = commands.filter(_.id === command.id)
  //      .map(c => c)
  //      .update(command.id, command.command, command.content, command.tips, command.status, command.parseType)
  //    Await.result(db.run(updateAction), 10.seconds)
  //    Future.successful(Done)
  //  }
  //
  //  /**
  //   * 修改状态
  //   *
  //   * @param id
  //   * @param status
  //   */
  //  def updateStatus(id: Long, status: Int): Unit = {
  //    val s = for {command <- commands if command.id === id} yield command.status
  //    val updateAction = s.update(status)
  //    println(Await.result(db.run(updateAction), 10.seconds))
  //  }
  //
  //  /**
  //   * 根据command查询是否有记录
  //   *
  //   * @param command
  //   * @return
  //   */
  //  def queryByCommand(command: String): Option[BotCommand] = {
  //    val action = commands.filter(_.command === command).result.headOption
  //    val result = Await.result(db.run(action), 10.seconds)
  //    if (!result.isEmpty) {
  //      Option(result.get)
  //    } else {
  //      None
  //    }
  //  }
  //
  //  /**
  //   * 查询所有
  //   *
  //   * @return
  //   */
  //  def queryAll(): Future[Option[Seq[BotCommand]]] = {
  //    val action = commands.result
  //    val result = Await.result(db.run(action), 10.seconds)
  //    Future.successful(Option(result.map(c => c)))
  //  }
  //
  //  /**
  //   * 根据ID删除
  //   *
  //   * @param id
  //   * @return
  //   */
  //  def delete(id: Long): Boolean = {
  //    val q = commands.filter(_.id === id)
  //    val action = q.delete
  //    val affectedRowsCount: Future[Int] = db.run(action)
  //    val result = Await.result(affectedRowsCount, 10.seconds)
  //    result > 0
  //  }
}

