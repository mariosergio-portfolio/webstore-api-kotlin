package com.mycompany.webstore.domain.model

import java.math.BigDecimal
import java.util.UUID

data class ShippingMethod(
    val id: UUID,
    val name: String,
    val cost: Money,
    val estimatedDays: Int,
    val active: Boolean,
)
