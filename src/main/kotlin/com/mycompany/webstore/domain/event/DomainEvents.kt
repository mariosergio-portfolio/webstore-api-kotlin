package com.mycompany.webstore.domain.event

import java.time.Instant
import java.util.UUID

sealed class DomainEvent { abstract val occurredAt: Instant }

data class OrderPlaced(val orderId: UUID, val customerId: UUID, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class OrderPaid(val orderId: UUID, val paymentId: UUID, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class OrderCancelled(val orderId: UUID, val customerId: UUID, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class OrderShipped(val orderId: UUID, val trackingNumber: String?, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class StockReduced(val productId: UUID, val orderId: UUID, val delta: Int, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class StockRestored(val productId: UUID, val orderId: UUID, val delta: Int, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class StockBelowThreshold(val productId: UUID, val stockQuantity: Int, val reorderPoint: Int, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class CartMerged(val customerId: UUID, val anonymousCartId: UUID, override val occurredAt: Instant = Instant.now()) : DomainEvent()
data class PaymentFailed(val orderId: UUID, val paymentId: UUID, override val occurredAt: Instant = Instant.now()) : DomainEvent()
