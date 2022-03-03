package com.example.dao

import org.slf4j.{Logger, LoggerFactory}

trait Logable {
  lazy val logger: Logger = LoggerFactory.getLogger(this.getClass.getSimpleName)
}
