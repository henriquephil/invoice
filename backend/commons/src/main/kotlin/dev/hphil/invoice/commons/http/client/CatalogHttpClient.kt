package dev.hphil.invoice.commons.http.client

import dev.hphil.invoice.commons.dtos.catalog.CustomerResponse
import dev.hphil.invoice.commons.dtos.catalog.ItemResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import java.util.UUID

class CatalogHttpClient(private val httpClient: HttpClient, private val baseUrl: String) {

    suspend fun getItem(id: UUID): ItemResponse {
        return httpClient.get(baseUrl) {
            url {
                appendPathSegments("items", id.toString())
            }
        }.body()
    }

    suspend fun getCustomer(id: UUID): CustomerResponse {
        return httpClient.get(baseUrl) {
            url {
                appendPathSegments("customers", id.toString())
            }
        }.body()
    }
}
