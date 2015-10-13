import de.heikoseeberger.sbtheader.HeaderPattern

name := "sbt-source-generators"

scalaVersion := "2.11.7"

scalacOptions ++= List("-feature", "-deprecation", "-unchecked", "-Xlint")

val header =
"""/*
  | * Some header
  | */
  |""".stripMargin

headers := Map(
  "scala" -> (HeaderPattern.cStyleBlockComment, header),
  "java" -> (HeaderPattern.cStyleBlockComment, header)
)

enablePlugins(AutomateHeaderPlugin)

sourceGenerators in Compile += Def.task {
  Generator.generate((sourceManaged in Compile).value, Some(header))
}.taskValue