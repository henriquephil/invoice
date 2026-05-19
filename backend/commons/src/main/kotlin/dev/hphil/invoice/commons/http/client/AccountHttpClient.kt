package dev.hphil.invoice.commons.http.client

import dev.hphil.invoice.commons.dtos.account.AccountAddressResponse
import dev.hphil.invoice.commons.dtos.account.AccountBankingResponse
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.UUID

class AccountHttpClient(private val httpClient: HttpClient, private val baseUrl: String) {

    suspend fun getAccount(accountId: UUID): AccountResponse {
        return httpClient.get(baseUrl) {
            url {
                appendPathSegments(accountId.toString())
            }
        }.body()
    }

    suspend fun getAccountAddress(accountId: UUID): AccountAddressResponse {
        return httpClient.get(baseUrl) {
            url {
                appendPathSegments(accountId.toString(), "address")
            }
        }.body()
    }

    suspend fun getAccountBanking(accountId: UUID): AccountBankingResponse {
        return httpClient.get(baseUrl) {
            url {
                appendPathSegments(accountId.toString(), "banking")
            }
        }.body()
    }
}