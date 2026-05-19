package dev.hphil.invoice.commons.config.nyi

import dev.hphil.invoice.commons.config.HeaderPropagation
import io.github.flaxoos.ktor.server.plugins.kafka.*
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader

fun Application.configureKafka() {
    install(Kafka) {
        schemaRegistryUrl = "my.schemaRegistryUrl"
        val myTopic = TopicName.named("my-topic")
        topic(myTopic) {
            partitions = 1
            replicas = 1
            configs {
                messageTimestampType = MessageTimestampType.CreateTime
            }
        }
        common { // <-- Define common properties
            bootstrapServers = listOf("my-kafka")
            retries = 1
            clientId = "my-client-id"
        }
        admin { } // <-- Creates an admin client
        producer { // <-- Creates a producer
            clientId = "my-client-id"
        }
        consumer { // <-- Creates a consumer
            groupId = "my-group-id"
            clientId = "my-client-id-override" //<-- Override common properties
        }
        consumerConfig {
            consumerRecordHandler(myTopic) { record ->
//                val propagationContext = ContextHeaders(record.headers())
//                withContext(propagationContext) {
//                    myBusinessService.process(record.value())
//                }
            }
        }
        registerSchemas {
            using { // <-- optionally provide a client, by default CIO is used
                HttpClient()
            }
            // MyRecord::class at myTopic // <-- Will register schema upon startup
        }
    }
}

//suspend fun handleKafkaMessage(record: ConsumerRecord<String, String>) {
//    val kafkaHeaders = record.headers()
//    val authHeader = kafkaHeaders.lastHeader("authorization")
    // tod validate auth header integrity (only signature, not expiration)
//    withContext(ContextHeaders(kafkaHeaders)) {
//        doSomething()
//    }
//}
//suspend fun <K, V> KafkaProducer<K, V>.sendWithContext(topic: String, key: K, value: V) {
//    val contextHeaders = currentCoroutineContext()[ContextHeaders]?.headers
//
//    val record = ProducerRecord(topic, key, value)
//
//     Adiciona os headers do contexto ao record do Kafka
//    contextHeaders?.forEach { (key, values) ->
//        values.forEach { v ->
//            record.headers().add(RecordHeader(key, v.toByteArray()))
//        }
//    }
//
//    this.send(record).get() // Ou use a versão suspensa se o seu driver suportar
//}


