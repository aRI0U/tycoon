libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"

val swing = "org.scala-lang.modules" %% "scala-swing" % "2.0.1"
lazy val root = (project in file(".")).
settings(
name := "Tycoon", libraryDependencies += swing
)

fork in run := true

scalacOptions ++= Seq("-unchecked", "-deprecation","-feature")
