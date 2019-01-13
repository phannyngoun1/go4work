name := """4work"""

version := "1.1-SNAPSHOT"

scalaVersion := "2.12.7"

crossScalaVersions := Seq("2.11.12", "2.12.6")

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)

lazy val api = (project in file("modules/api"))
  .settings(Common.projectSettings)



lazy val slick = (project in file("modules/slick"))
  .settings(Common.projectSettings)
  .aggregate(api)
  .dependsOn(api)

lazy val common =  (project in file("modules/common"))
  .settings(Common.projectSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % Common.akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % Common.akkaVersion
    )
  )


lazy val inventory =  (project in file("modules/inventory"))
  .settings(Common.projectSettings)
  .dependsOn(common)

lazy val ticket =  (project in file("modules/ticket"))
  .settings(Common.projectSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % Common.akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % Common.akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % Common.akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.19",
      //"ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.4.0",
      "org.iq80.leveldb" % "leveldb" % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",

      "mysql" % "mysql-connector-java" % "5.1.42",
    )
  )
  .aggregate(common)
  .dependsOn(common)


lazy val workflow =  (project in file("modules/workflow"))
  .settings(Common.projectSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % Common.akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % Common.akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % Common.akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.19",
      //"ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.4.0",
      "org.iq80.leveldb" % "leveldb" % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",

      "mysql" % "mysql-connector-java" % "5.1.42",
    )
  )
  .aggregate(common)
  .dependsOn(common)

lazy val root = (project in file("."))
  .settings(Common.projectSettings)
  .enablePlugins(PlayScala)
  .aggregate(slick, ticket)
  .dependsOn(slick, ticket)


scalacOptions += "-Ypartial-unification"
TwirlKeys.templateImports += "com.example.user.User"

libraryDependencies += guice
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Automatic database migration available in testing
fork in Test := true
libraryDependencies += "org.flywaydb" % "flyway-core" % "5.1.1"
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws" % "2.6.15" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

