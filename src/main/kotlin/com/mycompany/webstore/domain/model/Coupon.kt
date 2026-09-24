package com.mycompany.webstore.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

enum class DiscountType { PERCENTAGE, FIXED_AMOUNT }

data class Coupon(
    val id: UUID,
    val code: String,
    val discountType: DiscountType,
    val value: BigDecimal,
    val minOrderAmount: BigDecimal?,
    val maxUses: Int?,
    val usedCount: Int,
    val expiresAt: Instant?,
    val active: Boolean,
) {
    fun isValid(orderSubtotal: BigDecimal, now: Instant): Boolean {
        if (!active) return false
        if (expiresAt != null && now.isAfter(expiresAt)) return false
        if (maxUses != null && usedCount >= maxUses) return false
        if (minOrderAmount != null && orderSubtotal < minOrderAmount) return false
        return true
    }

    fun applyTo(subtotal: Money): Money {
        val discount = when (discountType) {
            DiscountType.PERCENTAGE   -> subtotal.amount * (value / BigDecimal(100))
            DiscountType.FIXED_AMOUNT -> value
        }
        return Money(discount.coerceAtMost(subtotal.amount), subtotal.currency)
    }
}
