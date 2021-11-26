package cz.kamenitxan.fluvii.core.configuration

import cz.kamenitxan.fluvii.core.configuration.ConfigurationInitializer._

object Config {
	val port2: String = ic[String]("port")
	val port: Int = ic[java.lang.Integer]("port")
	val port3: Boolean = ic[java.lang.Boolean]("MAIL.enabled")
	val deployMode: DeployMode = ic[DeployMode]("deployMode")
}

