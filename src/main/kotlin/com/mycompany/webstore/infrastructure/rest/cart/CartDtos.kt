package com.mycompany.webstore.infrastructure.rest.cart

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class AddItemRequest(@field:NotNull val productId: UUID, @field:Min(1) val quantity: Int)
data class UpdateItemRequest(@field:Min(0) val quantity: Int)
data class ApplyCouponRequest(@field:NotBlank val couponCode: String)

data class CartItemResponse(val id: UUID, val productId: UUID, val quantity: Int, val unitPrice: BigDecimal, val currency: String, val lineTotal: BigDecimal)
data class CartResponse(val id: UUID, val items: List<CartItemResponse>, val couponId: UUID?, val updatedAt: Instant)
