package com.mycompany.webstore.domain.model

import java.time.Instant
import java.util.UUID

enum class PaymentStatus { PENDING, SUCCEEDED, FAILED, REFUNDED }

data class Payment(
    val id: UUID,
    val orderId: UUID,
    val gateway: String,
    val gatewayReference: String?,
    val idempotencyKey: UUID,
    val amount: Money,
    val status: PaymentStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
