package cz.kamenitxan.fluvii.logging

object LogService {

	def getRepository: LogRepository = {
		LoggingSetting.logRepository
	}

	def getLogs:Seq[Log] = {
		LoggingSetting.logRepository.getLogs
	}

}
