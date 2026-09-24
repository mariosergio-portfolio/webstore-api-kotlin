package com.mycompany.webstore.domain.model

import java.time.Instant
import java.util.UUID

enum class InventoryChangeType { DEDUCTION, RESTORATION, ADJUSTMENT }

data class InventoryAuditLog(
    val id: UUID,
    val productId: UUID,
    val changeType: InventoryChangeType,
    val delta: Int,
    val reason: String?,
    val actor: String?,
    val orderId: UUID?,
    val createdAt: Instant,
)
