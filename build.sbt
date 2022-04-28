lazy val akkaHttpVersion = "10.2.8"
lazy val akkaVersion = "2.6.18"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

enablePlugins(AkkaGrpcPlugin)

val `kamon-version` = "2.5.1+17-26edf68e"

lazy val root = (project in file(".")).
  enablePlugins(JavaAgent).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.15"
    )),
    name := "akka-monitor-demo",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      //kafka
      "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.5",


      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.1.4" % Test,

      // https://kamon.io/docs/latest/guides/how-to/start-with-the-kanela-agent/#running-from-sbt
//      "io.kamon" %% "kamon-bundle" % "2.5.0", // 不用这种方式 使用agent
      "io.github.mofei100" %% "kamon-core" % `kamon-version`,
      "io.kamon" %% "kamon-akka" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.kamon" %% "kamon-system-metrics" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.kamon" %% "kamon-executors" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.github.mofei100" %% "kamon-akka-http" % `kamon-version` exclude("io.github.mofei100", "kamon-core_2.12") exclude("io.github.mofei100", "kamon-akka_2.12"),
      "io.kamon" %% "kamon-jdbc" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.kamon" %% "kamon-kafka" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.kamon" %% "kamon-logback" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.kamon" %% "kamon-jaeger" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.kamon" %% "kamon-status-page" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.github.mofei100" %% "kamon-akka-grpc" % `kamon-version` exclude("io.github.mofei100", "kamon-core_2.12") exclude("io.github.mofei100", "kamon-akka_2.12"),
      "io.kamon" %% "kamon-redis" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.kamon" %% "kamon-prometheus" % "2.5.0" exclude("io.kamon", "kamon-core_2.12"),
      "io.github.mofei100" %% "kamon-sttp-client3" % `kamon-version` exclude("io.github.mofei100", "kamon-core_2.12"),
      //slick
      "com.typesafe.slick" %% "slick" % "3.3.2",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
      "mysql" % "mysql-connector-java" % "8.0.9-rc",
      "org.xerial" % "sqlite-jdbc" % "3.23.1",
      //redis
      "redis.clients" % "jedis" % "3.3.0",
      //okhttp
      "com.squareup.okhttp3" % "okhttp" % "4.9.2",
      "com.softwaremill.sttp.client3" %% "core" % "3.5.2",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % "3.5.2",
      "com.softwaremill.sttp.client3" %% "spray-json" % "3.5.2",
      "io.kamon"              %  "kanela-agent"    % "1.0.12"
    )
  )