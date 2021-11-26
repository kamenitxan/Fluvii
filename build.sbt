import sbt.Keys.organization

val V = new {
	val Scala = "3.1.0"

	val laminar = "0.13.1"
	val http4s = "0.23.4"
	val sttp = "3.3.13"
	val circe = "0.14.1"
	val decline = "2.1.0"
	val weaver = "0.7.6"
	val doobieVersion = "1.0.0-RC1"
	val log4jVersion = "2.14.1"
}

scalaVersion := V.Scala
name := "fluvii"
version := "0.1-SNAPSHOT"

val Dependencies = new {
	private val http4sModules =
		Seq("dsl", "ember-client", "ember-server", "circe").map("http4s-" + _)

	private val sttpModules = Seq("core", "circe")

	lazy val frontend = Seq(
		libraryDependencies ++=
			Seq(
				"com.softwaremill.sttp.client3" %%% "core" % V.sttp,
				"com.softwaremill.sttp.client3" %%% "circe" % V.sttp,
				"com.raquo" %%% "laminar" % V.laminar
			)
	)

	lazy val backend = Seq(
		libraryDependencies ++=
			http4sModules.map("org.http4s" %% _ % V.http4s) ++
				Seq(
					"com.monovore" %% "decline" % V.decline,
					"org.xerial" % "sqlite-jdbc" % "3.23.1",
					"org.tpolecat" %% "doobie-core" % V.doobieVersion,
					"org.tpolecat" %% "doobie-hikari" % V.doobieVersion,
					"org.apache.logging.log4j" % "log4j-slf4j-impl" % V.log4jVersion,
					"com.lihaoyi" %% "sourcecode" % "0.2.7"
				)
	)

	lazy val shared = Def.settings(
		libraryDependencies += "io.circe" %%% "circe-core" % V.circe
	)

	lazy val tests = Def.settings(
		libraryDependencies += "com.disneystreaming" %%% "weaver-cats" % V.weaver % Test,
		testFrameworks += new TestFramework("weaver.framework.CatsEffect")
	)
}

lazy val root =
	(project in file(".")).aggregate(frontend, backend, shared.js, shared.jvm)

lazy val frontend = (project in file("modules/frontend"))
	.dependsOn(shared.js)
	.enablePlugins(ScalaJSPlugin)
	.settings(scalaJSUseMainModuleInitializer := true)
	.settings(
		Dependencies.frontend,
		Dependencies.tests,
		Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
	)
	.settings(
		commonBuildSettings,
		name := "fluvii-fe"
	)

lazy val backend = (project in file("modules/backend"))
	.dependsOn(shared.jvm)
	.settings(Dependencies.backend)
	.settings(Dependencies.tests)
	.settings(commonBuildSettings)
	.enablePlugins(JavaAppPackaging)
	.enablePlugins(DockerPlugin)
	.settings(
		name := "fluvii",
		Test / fork := true,
		Universal / mappings += {
			val appJs = (frontend / Compile / fullOptJS).value.data
			appJs -> ("lib/prod.js")
		},
		Universal / javaOptions ++= Seq(
			"--port 8080",
			"--mode prod"
		),
		Docker / packageName := "laminar-http4s-example"
	)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
	.crossType(CrossType.Pure)
	.in(file("modules/shared"))
	.jvmSettings(Dependencies.shared)
	.jsSettings(Dependencies.shared)
	.jsSettings(commonBuildSettings)
	.jvmSettings(commonBuildSettings)
	.settings(
		name := "fluvii-shared"
	)

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
ThisBuild / semanticdbEnabled := true
ThisBuild / scalacOptions += "-deprecation"

lazy val fastOptCompileCopy = taskKey[Unit]("")

val jsPath = "modules/backend/src/main/resources"

fastOptCompileCopy := {
	val source = (frontend / Compile / fastOptJS).value.data
	IO.copyFile(
		source,
		baseDirectory.value / jsPath / "dev.js"
	)
}

lazy val fullOptCompileCopy = taskKey[Unit]("")

fullOptCompileCopy := {
	val source = (frontend / Compile / fullOptJS).value.data
	IO.copyFile(
		source,
		baseDirectory.value / jsPath / "prod.js"
	)

}

lazy val commonBuildSettings: Seq[Def.Setting[_]] = Seq(
	scalaVersion := V.Scala,
	organization := "cz.kamenitxan",
	name := "Fluvii",
	startYear := Some(2021)
)

addCommandAlias("runDev", ";fastOptCompileCopy; backend/reStart --mode dev")
addCommandAlias("runProd", ";fullOptCompileCopy; backend/reStart --mode prod")

val scalafixRules = Seq(
	"OrganizeImports",
	"DisableSyntax",
	"LeakingImplicitClassVal",
	"NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
	"clean",
	"backend/compile",
	"backend/test",
	"frontend/compile",
	"frontend/fastOptJS",
	"frontend/test",
	s"scalafix --check $scalafixRules"
).mkString(";")

val PrepareCICommands = Seq(
	s"scalafix $scalafixRules"
).mkString(";")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)
