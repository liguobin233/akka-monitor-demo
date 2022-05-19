package com.example

import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{ Flow, Source }

object WebsocketRoute {

  val greeterWebSocketService =
    Flow[Message]
      .collect {
        case tm: TextMessage => TextMessage(Source.single("Hello ") ++ tm.textStream)
      }

  //#websocket-routing
  val route =
    path("ws") {
      get {
        handleWebSocketMessages(greeterWebSocketService)
      }
    }
  //#websocket-routing
}