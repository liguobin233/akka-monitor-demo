addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "2.1.3")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.6")
// 这种方式只有执行sbt run时才会开启kamon
addSbtPlugin("io.kamon" % "sbt-kanela-runner" % "2.0.14")