package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Payment
import java.util.UUID

interface PaymentPort {
    fun initiate(orderId: UUID, gateway: String): Payment
    fun handleStripeWebhook(payload: String, signature: String)
    fun handlePayPalWebhook(payload: String, signature: String)
    fun handleMercadoPagoWebhook(payload: String, signature: String)
    fun getByOrderId(orderId: UUID): Payment
    fun initiateRefund(orderId: UUID): Payment
}
