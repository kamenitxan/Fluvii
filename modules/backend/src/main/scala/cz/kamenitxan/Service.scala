package cz.kamenitxan

import cats.effect.IO
import doobie.util.transactor.Transactor
import example.shared.Protocol.GetSuggestions
import doobie.*
import doobie.implicits.*
import cats.*
import cats.effect.*
import cats.implicits.*
import example.shared.Protocol.GetSuggestions.{Request, Response}

trait Service {

	def getSuggestions(
											request: GetSuggestions.Request
										): IO[GetSuggestions.Response]

	def test(): IO[String]
}

object ServiceImpl extends Service {
	// this would come from your database
	// unless you're at a VC pitch meeting and you need
	// to show the completely working app
	// then by all means keep it hardcoded, thank me later
	private val things = Seq(
		"This",
		"That",
		"maybe this",
		"maybe that"
	)


	def getSuggestions(
											request: GetSuggestions.Request
										): IO[GetSuggestions.Response] = {
		import GetSuggestions.*

		request match
			case Request(search, Some(false) | None) =>
				IO.pure(Response(things.filter(_.contains(search))))
			case Request(search, Some(true)) =>
				IO.pure(Response(things.filter(_.startsWith(search))))
	}

	def test() = {
		import cats.effect.unsafe.implicits.global

		val xa = Transactor.fromDriverManager[IO](
			"org.sqlite.JDBC", // driver classname
			"jdbc:sqlite:fluvii.sqlite", // connect URL (driver-specific)
			"", // user
			"" // password
		)

		val program2 = sql"select size, name from table_name".query[Thing]
		val io2 = program2.unique.transact(xa)
		val res: Any = io2.unsafeRunSync()
		println(res)

		val y = xa.yolo
		import y._
		program2.check.unsafeRunSync()

		IO.pure(res.toString)
	}

}

case class Thing(name: String, size: Int)