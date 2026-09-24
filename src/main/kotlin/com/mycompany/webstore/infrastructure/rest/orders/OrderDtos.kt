package com.mycompany.webstore.infrastructure.rest.orders

import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.infrastructure.rest.catalog.MoneyDto
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class OrderItemResponse(val id: UUID, val productId: UUID, val sku: String, val name: String, val quantity: Int, val unitPrice: MoneyDto, val lineTotal: MoneyDto)
data class OrderResponse(
    val id: UUID, val customerId: UUID, val status: OrderStatus,
    val items: List<OrderItemResponse>, val subtotal: MoneyDto, val discountAmount: MoneyDto,
    val shippingCost: MoneyDto, val total: MoneyDto, val placedAt: Instant, val updatedAt: Instant,
    val trackingCarrier: String?, val trackingNumber: String?,
)

data class AdvanceStatusRequest(val status: OrderStatus, val trackingCarrier: String?, val trackingNumber: String?)

fun Order.toResponse() = OrderResponse(
    id = id, customerId = customerId, status = status,
    items = items.map { OrderItemResponse(it.id, it.productId, it.sku, it.name, it.quantity, MoneyDto(it.unitPrice.amount, it.unitPrice.currency), MoneyDto(it.lineTotal.amount, it.lineTotal.currency)) },
    subtotal = MoneyDto(subtotal.amount, subtotal.currency),
    discountAmount = MoneyDto(discountAmount.amount, discountAmount.currency),
    shippingCost = MoneyDto(shippingCost.amount, shippingCost.currency),
    total = MoneyDto(total.amount, total.currency),
    placedAt = placedAt, updatedAt = updatedAt,
    trackingCarrier = trackingCarrier, trackingNumber = trackingNumber,
)
