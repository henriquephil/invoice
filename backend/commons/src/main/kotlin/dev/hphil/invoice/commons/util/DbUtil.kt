package dev.hphil.invoice.commons.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun <T> txRead(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        transaction(readOnly = true) {
            block()
        }
    }
suspend fun <T> txWrite(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        transaction {
            block()
        }
    }