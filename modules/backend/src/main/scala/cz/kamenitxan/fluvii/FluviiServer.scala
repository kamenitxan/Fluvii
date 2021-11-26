package cz.kamenitxan.fluvii

import cats.effect.*
import com.comcast.ip4s.{Host, Port}
import cz.kamenitxan.fluvii.core.configuration.{Config, ConfigurationInitializer, DeployMode}
import cz.kamenitxan.fluvii.logging.Logger
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.GZip

import java.io.File

object FluviiServer extends IOApp {

	def run(args: List[String]): IO[ExitCode] = {
		val arguments = args.map(a => {
			val split = a.split("=")
			split.length match {
				case 1 => split(0) -> ""
				case 2 => split(0) -> split(1)
			}
		})
		val configName = arguments.find(a => a._1 == "jakonConfig").map(a => a._2)
		val configFile = if (configName.nonEmpty) new File(configName.get) else null
		ConfigurationInitializer.init(configFile)


		val status = IO.delay({
			println("started")
			Logger.info(s"Running server on http://0.0.0.0:${Config.port} (mode: ${Config.deployMode})")
		})

		val frontendJS = Config.deployMode match {
			case _: DeployMode.DEVEL.type => "dev.js"
			case _: DeployMode.TESTING.type => "dev.js"
			case _: DeployMode.PRODUCTION.type => "prod.js"
		}
		val routes = new Routes(ServiceImpl, frontendJS).routes

		val app = GZip(routes)

		Logger.critical("test")

		EmberServerBuilder
			.default[IO]
			.withPort(Port.fromInt(Config.port).get)
			.withHost(Host.fromString("0.0.0.0").get)
			.withHttpApp(app.orNotFound)
			.build
			.use(_ => status *> IO.never)
			.as(ExitCode.Success)

	}


}
