package dev.hphil.invoice.commons.config.nyi

//class ConfigParam(private val ssmClient: SsmClient) {
//    private val logger = LoggerFactory.getLogger(ConfigParam::class.java)
//
//    fun loadParametersByPath(pathPrefix: String): Map<String, String> {
//        val parameters = mutableMapOf<String, String>()
//        var nextToken: String? = null
//
//        logger.info("Attempting to load parameters from AWS SSM path: {}", pathPrefix)
//
//        try {
//            do {
//                val request = GetParametersByPathRequest.builder()
//                    .path(pathPrefix)
//                    .withDecryption(true)
//                    .recursive(true)
//                    .maxResults(10)
//                    .nextToken(nextToken)
//                    .build()
//
//                val response = ssmClient.getParametersByPath(request)
//                response.parameters().forEach { parameter ->
//                    val nameWithoutPrefix = parameter.name().removePrefix(pathPrefix)
//                    parameters[nameWithoutPrefix] = parameter.value()
//                    logger.debug("Loaded SSM parameter: {}", nameWithoutPrefix)
//                }
//                nextToken = response.nextToken()
//            } while (nextToken != null)
//            logger.info("Successfully loaded {} parameters from AWS SSM.", parameters.size)
//        } catch (e: Exception) {
//            logger.error("Failed to load parameters from AWS SSM path '{}': {}", pathPrefix, e.message, e)
//            // Optionally, rethrow or handle specific exceptions if critical
//        }
//        return parameters
//    }
//}
