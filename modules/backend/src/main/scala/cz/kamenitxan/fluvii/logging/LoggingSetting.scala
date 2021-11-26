package cz.kamenitxan.fluvii.logging

import cz.kamenitxan.fluvii.core.configuration.ConfigurationInitializer._

/**
  * Created by TPa on 16/11/2019.
  */
object LoggingSetting {

	val maxLimit: Int = ic[java.lang.Integer]("LOGGING.maxLimit", default = 100000)

	val softLimit: Int = ic[java.lang.Integer]("LOGGING.softLimit", default = 50000)

	val maxCriticalAge: Int = ic[java.lang.Integer]("LOGGING.maxCriticalAge", default = 43200)

	val maxErrorAge: Int = ic[java.lang.Integer]("LOGGING.maxErrorAge", default = 14400)

	val maxWarningAge: Int = ic[java.lang.Integer]("LOGGING.maxWarningAge", default = 7200)

	val maxInfoAge: Int = ic[java.lang.Integer]("LOGGING.maxInfoAg", default = 1440)

	val maxDebugAge: Int = ic[java.lang.Integer]("LOGGING.maxDebugAge", default = 60)

	val logRepository: LogRepository = {
		val cn: String = ic[String]("LOGGING.logRepository", default = "cz.kamenitxan.fluvii.logging.InMemoryLogRepository")
		Class.forName(cn).getDeclaredConstructor().newInstance().asInstanceOf[LogRepository]
	}


}
