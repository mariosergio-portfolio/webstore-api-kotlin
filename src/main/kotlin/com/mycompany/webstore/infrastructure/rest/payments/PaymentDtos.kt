package com.mycompany.webstore.infrastructure.rest.payments

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class InitiatePaymentRequest(@field:NotNull val orderId: UUID, @field:NotBlank val gateway: String)
data class PaymentResponse(val id: UUID, val orderId: UUID, val gateway: String, val status: String, val amount: BigDecimal, val currency: String, val createdAt: Instant)
