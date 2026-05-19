package dev.hphil.invoice.catalog

import cz.jirutka.rsql.parser.RSQLParser
import dev.hphil.invoice.catalog.database.Item
import dev.hphil.invoice.catalog.database.ItemTable
import dev.hphil.invoice.catalog.database.ItemType
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.catalog.CreateItemRequest
import dev.hphil.invoice.commons.dtos.catalog.ItemResponse
import dev.hphil.invoice.commons.dtos.catalog.UpdateItemRequest
import dev.hphil.invoice.commons.util.ExposedRSQLVisitor
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import java.util.*

class ItemService {
    suspend fun create(request: CreateItemRequest, account: AccountResponse): UUID {
        // todo validate
        return txWrite {
            Item.new(account.id,
                ItemType.valueOf(request.type),
                request.name,
                request.measureUnit,
                request.unitPrice,
                request.currency
            )
        }.id.value
    }

    suspend fun getAll(account: AccountResponse, rsqlFilter: String?): List<ItemResponse> {
        val filter = if (rsqlFilter.isNullOrBlank())
            Op.TRUE
        else
            RSQLParser().parse(rsqlFilter).accept(Item.filterDict)
        return txRead { Item.findByAccountId(account.id, filter) }.map { it.toItemResponse() }
    }

    suspend fun get(id: UUID, account: AccountResponse): ItemResponse {
        val item = txRead { Item.findByIdAndAccountId(id, account.id) }
            ?: throw NoSuchElementException("Account not found")
        return item.toItemResponse()
    }

    suspend fun update(id: UUID, request: UpdateItemRequest, account: AccountResponse) {
        txWrite {
            Item.findByIdAndAccountId(id, account.id)?.apply {
                request.name?.let { name = it }
                request.measureUnit?.let { measureUnit = it }
                request.unitPrice?.let { unitPrice = it }
                request.currency?.let { currency = it }
                request.type?.let { type = ItemType.valueOf(it) }
            }
        }
    }
}

private fun Item.toItemResponse(): ItemResponse {
    return ItemResponse(id.value, type.name, name, measureUnit, unitPrice, currency)
}
