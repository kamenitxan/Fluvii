package cz.kamenitxan.fluvii

import cats.effect.IO
import cz.kamenitxan.fluvii.logging.Logger
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*

/**
 * Created by TPa on 02.12.2021.
 */
object TestService {

	val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

		case GET -> Root / "test" =>
			Logger.debug("test ok")
			Ok("test ok")
		case GET -> Root / "fail" =>
			Logger.debug("test fail")
			throw new IllegalAccessException()
			Ok("test fail")

	}

}
