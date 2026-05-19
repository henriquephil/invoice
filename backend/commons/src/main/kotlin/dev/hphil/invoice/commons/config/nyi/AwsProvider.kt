package dev.hphil.invoice.commons.config.nyi

//class AwsProvider(config: ApplicationConfig) {
//    private val region = config.propertyOrNull("aws.region")?.getString() ?: "us-east-1"
//    private val endpointUrl = config.propertyOrNull("aws.endpointUrl")?.getString()
//    private val accessKey = config.propertyOrNull("aws.accessKeyId")?.getString() ?: "test"
//    private val secretKey = config.propertyOrNull("aws.secretAccessKey")?.getString() ?: "test"
//
//    private val awsCredentialsProvider: AwsCredentialsProvider
//
//    init {
//        val awsCredentials = AwsBasicCredentials.create(accessKey, secretKey)
//        awsCredentialsProvider = AwsCredentialsProvider { awsCredentials }
//    }
//
//    private val logger = LoggerFactory.getLogger(AwsProvider::class.java)
//
//    fun ssmClient(): SsmClient {
//        val clientBuilder = SsmClient.builder()
//            .region(Region.of(region))
//            .credentialsProvider(awsCredentialsProvider) // todo prod provider
//        endpointUrl?.let {
//            clientBuilder.endpointOverride(URI.create(it))
//            logger.info("AWS SSM Client configured with endpoint override: {}", it)
//        }
//        return clientBuilder.build()
//    }
//}
