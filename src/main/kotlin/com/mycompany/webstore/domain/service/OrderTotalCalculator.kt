package com.mycompany.webstore.domain.service

import com.mycompany.webstore.domain.model.*
import java.math.BigDecimal
import java.time.Instant

object OrderTotalCalculator {
    fun calculate(items: List<CartItem>, coupon: Coupon?, shippingCost: Money, currency: String): OrderTotals {
        val subtotal = items.fold(Money.zero(currency)) { acc, item -> acc + (item.unitPrice * item.quantity) }
        val discountAmount = coupon?.takeIf { it.isValid(subtotal.amount, Instant.now()) }
            ?.applyTo(subtotal) ?: Money.zero(currency)
        val total = (subtotal - discountAmount) + shippingCost
        return OrderTotals(subtotal, discountAmount, shippingCost, total)
    }
}

data class OrderTotals(
    val subtotal: Money,
    val discountAmount: Money,
    val shippingCost: Money,
    val total: Money,
)
