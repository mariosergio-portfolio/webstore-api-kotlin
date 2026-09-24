package com.mycompany.webstore.application.port.out

data class PaymentEvent(
    val gatewayReference: String,
    val status: PaymentEventStatus,
    val orderId: String?,
)

enum class PaymentEventStatus { SUCCEEDED, FAILED, REFUNDED }

interface PaymentGatewayPort {
    fun initiatePayment(orderId: String, amount: java.math.BigDecimal, currency: String, idempotencyKey: String): String
    fun initiateRefund(gatewayReference: String, amount: java.math.BigDecimal, currency: String): String
    fun parseWebhookEvent(payload: String, signature: String): PaymentEvent
}
