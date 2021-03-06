name := """play-java-rest-inventory"""

version := "1.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaCore,
  javaJpa,
  "javax.inject" % "javax.inject" % "1",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.hibernate" % "hibernate-entitymanager" % "3.6.10.Final",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.dbunit" % "dbunit" % "2.4.9",
  cache,
  javaWs,
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.5"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true