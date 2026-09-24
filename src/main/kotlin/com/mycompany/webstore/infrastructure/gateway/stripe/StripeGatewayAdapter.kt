package com.mycompany.webstore.infrastructure.gateway.stripe

import com.mycompany.webstore.application.port.out.PaymentEvent
import com.mycompany.webstore.application.port.out.PaymentEventStatus
import com.mycompany.webstore.application.port.out.PaymentGatewayPort
import jakarta.enterprise.context.ApplicationScoped
import java.math.BigDecimal

/**
 * Stub implementation — replace with real Stripe SDK calls.
 * Always verify the signature before processing.
 */
@ApplicationScoped
class StripeGatewayAdapter : PaymentGatewayPort {

    override fun initiatePayment(orderId: String, amount: BigDecimal, currency: String, idempotencyKey: String): String {
        // TODO: call Stripe PaymentIntents API
        // stripe.paymentIntents.create({ amount, currency, idempotencyKey })
        return "pi_stub_$orderId"
    }

    override fun initiateRefund(gatewayReference: String, amount: BigDecimal, currency: String): String {
        // TODO: call stripe.refunds.create({ payment_intent: gatewayReference })
        return "re_stub_$gatewayReference"
    }

    override fun parseWebhookEvent(payload: String, signature: String): PaymentEvent {
        // TODO: Webhook.constructEvent(payload, signature, webhookSecret)
        // Parse event.type and map to PaymentEventStatus
        return PaymentEvent(
            gatewayReference = "pi_stub",
            status = PaymentEventStatus.SUCCEEDED,
            orderId = null,
        )
    }
}
