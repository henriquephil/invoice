package dev.hphil.invoice.invoice.handler

import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.invoice.InvoiceSettingsRequest
import dev.hphil.invoice.commons.dtos.invoice.InvoiceSettingsResponse
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import dev.hphil.invoice.invoice.database.InvoiceSettings
import dev.hphil.invoice.invoice.service.SettingsService

class SettingsHandler(private val settingsService: SettingsService) {

    suspend fun getByAccount(account: AccountResponse): InvoiceSettingsResponse = txRead {
        settingsService.findOrCreate(account)
    }.toInvoiceSettingsResponse()

    suspend fun updateForAccount(account: AccountResponse, request: InvoiceSettingsRequest): InvoiceSettingsResponse = txWrite {
        settingsService.findOrCreate(account)
            .apply {
                request.currentInvoiceNumber?.let { currentInvoiceNumber = it }
            }
    }.toInvoiceSettingsResponse()

}

private fun InvoiceSettings.toInvoiceSettingsResponse(): InvoiceSettingsResponse = InvoiceSettingsResponse(
    currentInvoiceNumber
)
