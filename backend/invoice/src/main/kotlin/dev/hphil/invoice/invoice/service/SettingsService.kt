package dev.hphil.invoice.invoice.service

import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.invoice.database.InvoiceSettings

class SettingsService {

    fun numberIncrementAndGet(account: AccountResponse): Int {
        return findOrCreate(account).let { ++it.currentInvoiceNumber }
    }

    fun findOrCreate(account: AccountResponse): InvoiceSettings =
        InvoiceSettings.findByAccountId(account.id) ?: InvoiceSettings.new(account.id)
}
