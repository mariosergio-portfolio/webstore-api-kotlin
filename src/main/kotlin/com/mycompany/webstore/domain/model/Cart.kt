package com.mycompany.webstore.domain.model

import java.time.Instant
import java.util.UUID

data class Cart(
    val id: UUID,
    val customerId: UUID?,
    val sessionId: String?,
    val items: List<CartItem>,
    val couponId: UUID?,
    val updatedAt: Instant,
) {
    init {
        require((customerId != null) xor (sessionId != null)) {
            "Exactly one of customerId or sessionId must be set"
        }
    }

    val isEmpty: Boolean get() = items.isEmpty()
}

data class CartItem(
    val id: UUID,
    val cartId: UUID,
    val productId: UUID,
    val quantity: Int,
    val unitPrice: Money,
) {
    init {
        require(quantity >= 1) { "Quantity must be >= 1" }
    }
}
