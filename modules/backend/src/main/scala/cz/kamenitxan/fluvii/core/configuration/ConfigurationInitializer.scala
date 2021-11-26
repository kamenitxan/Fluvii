package cz.kamenitxan.fluvii.core.configuration

import cz.kamenitxan.fluvii.logging.LogRepository
import cz.kamenitxan.fluvii.utils.Utils
import org.slf4j.LoggerFactory

import java.io.{File, FileInputStream, IOException}
import java.util.Properties
import scala.collection.mutable
import scala.language.postfixOps
import scala.quoted.*
import scala.compiletime.erasedValue


object ConfigurationInitializer {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	private val conf = mutable.HashMap[String, String]()


	@throws[IOException]
	def init(configFile: File): Unit = {
		Utils.measured(runtime => s"Configuration scanned in $runtime ms") {
			if (configFile == null) {
				try {
					init(new File("jakon_config.properties"))
				} catch {
					case e: Exception =>
						logger.error("Config loading failed. Shutting down!", e)
						System.exit(-1)
				}
			} else {
				val input = new FileInputStream(configFile)
				val prop = new Properties
				prop.load(input)
				val e = prop.propertyNames
				while ( {
					e.hasMoreElements
				}) {
					val key = e.nextElement.asInstanceOf[String]
					val value = prop.getProperty(key).trim
					conf.put(key, value)
				}
			}
		}
	}

	transparent inline def ic[T](name: String, required: Boolean = true, default: T = null) = {
		println(s"Initializing $name")
		//conf(name.value)
		val value = conf.getOrElse(name, default)
		inline erasedValue[T] match
			case _: java.lang.Integer => conf.get(name).map(_.toInt).getOrElse(default.asInstanceOf[Int])
			case _: java.lang.Boolean => conf(name).toBoolean
			case _: String => value.toString
			case _: DeployMode => DeployMode.valueOf(value.asInstanceOf[String])
			case _ => throw new IllegalArgumentException
	}

}
