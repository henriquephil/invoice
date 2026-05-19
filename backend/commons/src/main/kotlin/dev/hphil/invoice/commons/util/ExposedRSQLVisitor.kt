package dev.hphil.invoice.commons.util

import cz.jirutka.rsql.parser.ast.*
import org.jetbrains.exposed.v1.core.BooleanColumnType
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.DecimalColumnType
import org.jetbrains.exposed.v1.core.DoubleColumnType
import org.jetbrains.exposed.v1.core.EntityIDColumnType
import org.jetbrains.exposed.v1.core.IColumnType
import org.jetbrains.exposed.v1.core.IntegerColumnType
import org.jetbrains.exposed.v1.core.LongColumnType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.java.UUIDColumnType
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.or
import java.math.BigDecimal
import java.util.UUID

class ExposedRSQLVisitor(
    private val columnMap: Map<String, Column<*>>
) : RSQLVisitor<Op<Boolean>, Nothing?> {

    override fun visit(node: AndNode, param: Nothing?): Op<Boolean> =
        node.children.map { it.accept(this) }.reduce { acc, op -> acc and op }

    override fun visit(node: OrNode, param: Nothing?): Op<Boolean> =
        node.children.map { it.accept(this) }.reduce { acc, op -> acc or op }

    override fun visit(node: ComparisonNode, param: Nothing?): Op<Boolean> {
        val column = columnMap[node.selector] ?: throw IllegalArgumentException("Attribute '${node.selector}' not included.")

        return buildExpression(column, node.operator, node.arguments)
    }

    private fun buildExpression(column: Column<*>, operator: ComparisonOperator, args: List<String>): Op<Boolean> {
        val firstArg = args.first()

        return when (operator) {
            RSQLOperators.EQUAL -> {
                if (firstArg == "null") column.isNull()
                else column.asAny().eq(convertValue(column, firstArg))
            }
            RSQLOperators.NOT_EQUAL -> {
                if (firstArg == "null") column.isNotNull()
                else column.asAny().neq(convertValue(column, firstArg))
            }
            RSQLOperators.GREATER_THAN -> {
                column.asComparable().greater(convertValueComparable(column, firstArg))
            }
            RSQLOperators.LESS_THAN -> {
                column.asComparable().less(convertValueComparable(column, firstArg))
            }
            RSQLOperators.GREATER_THAN_OR_EQUAL -> {
                column.asComparable().greaterEq(convertValueComparable(column, firstArg))
            }
            RSQLOperators.LESS_THAN_OR_EQUAL -> {
                column.asComparable().lessEq(convertValueComparable(column, firstArg))
            }
            RSQLOperators.IN -> column.asAny().inList(args.map { convertValue(column, it) })
            else -> throw UnsupportedOperationException("Operator $operator not supported.")
        }
    }

    private fun convertValue(column: Column<*>, value: String): Any {
        val columnType = column.columnType
        return when {
            columnType.nullable && value == "null" -> value
            columnType is EntityIDColumnType<*> -> convertValue(columnType.idColumn, value)
            columnType is UUIDColumnType -> UUID.fromString(value)
            columnType is DecimalColumnType -> BigDecimal(value)
            columnType is IntegerColumnType -> value.toInt()
            columnType is LongColumnType -> value.toLong()
            columnType is DoubleColumnType -> value.toDouble()
            columnType is BooleanColumnType -> value.toBoolean()
            else -> value
        }
    }

    private fun convertValueComparable(column: Column<*>, value: String): Comparable<Any> {
        val converted = convertValue(column, value)
        return converted as? Comparable<Any>
            ?: throw IllegalArgumentException(
                "Attribute '${column.name}' with value '$value' doesn't support comparable actions."
            )
    }

    @Suppress("UNCHECKED_CAST")
    private fun Column<*>.asAny() = this as Column<Any>@Suppress("UNCHECKED_CAST")

    private fun Column<*>.asComparable() = this as Column<Comparable<Any>>
}