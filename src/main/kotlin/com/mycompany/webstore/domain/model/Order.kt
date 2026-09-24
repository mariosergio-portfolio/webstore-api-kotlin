package com.mycompany.webstore.domain.model

import java.time.Instant
import java.util.UUID

enum class OrderStatus { PENDING, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

data class Order(
    val id: UUID,
    val customerId: UUID,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val subtotal: Money,
    val discountAmount: Money,
    val shippingCost: Money,
    val total: Money,
    val shippingAddress: Address,
    val billingAddress: Address?,
    val shippingMethodId: UUID?,
    val trackingCarrier: String?,
    val trackingNumber: String?,
    val paymentId: UUID?,
    val placedAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        val CANCELLABLE_STATUSES = setOf(OrderStatus.PENDING, OrderStatus.PAID)

        val VALID_TRANSITIONS = mapOf(
            OrderStatus.PENDING    to setOf(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID       to setOf(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
            OrderStatus.PROCESSING to setOf(OrderStatus.SHIPPED),
            OrderStatus.SHIPPED    to setOf(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED  to emptySet(),
            OrderStatus.CANCELLED  to emptySet(),
        )
    }

    fun canTransitionTo(next: OrderStatus): Boolean =
        VALID_TRANSITIONS[status]?.contains(next) == true
}

data class OrderItem(
    val id: UUID,
    val orderId: UUID,
    val productId: UUID,
    val sku: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Money,
    val lineTotal: Money,
) {
    init {
        require(quantity >= 1) { "Quantity must be >= 1" }
    }
}
