package cz.kamenitxan.fluvii.utils

import cats.data.Kleisli
import cats.effect.*
import cats.syntax.all.*
import cz.kamenitxan.fluvii.logging.Logger
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*

/**
 * Created by TPa on 29.11.2021.
 */
object LoggingMiddleware {

	def apply(service: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
		Utils.measuredF(time => Logger.debug(s"Processed ${req.uri} in $time ms")) {
			service(req).map(resp => {
				resp match {
					case Status.Successful(resp) =>
						Logger.info("REQ OK")
						resp
					case resp =>
						Logger.error("REQ FAIL")
						resp
				}

			})


		}

	}

}
