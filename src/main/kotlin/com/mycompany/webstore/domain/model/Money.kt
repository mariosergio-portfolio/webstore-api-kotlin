package com.mycompany.webstore.domain.model

import java.math.BigDecimal

data class Money(val amount: BigDecimal, val currency: String) {
    init {
        require(amount >= BigDecimal.ZERO) { "Amount must be non-negative" }
        require(currency.length == 3) { "Currency must be ISO 4217 (3 chars)" }
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return copy(amount = amount + other.amount)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return copy(amount = (amount - other.amount).coerceAtLeast(BigDecimal.ZERO))
    }

    operator fun times(quantity: Int): Money = copy(amount = amount * BigDecimal(quantity))

    companion object {
        fun of(amount: BigDecimal, currency: String = "USD") = Money(amount, currency)
        fun zero(currency: String = "USD") = Money(BigDecimal.ZERO, currency)
    }
}
