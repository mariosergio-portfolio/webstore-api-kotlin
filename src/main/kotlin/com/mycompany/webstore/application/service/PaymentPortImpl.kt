package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.PaymentPort
import com.mycompany.webstore.application.port.out.OrderRepository
import com.mycompany.webstore.application.port.out.PaymentGatewayPort
import com.mycompany.webstore.application.port.out.PaymentEventStatus
import com.mycompany.webstore.application.port.out.PaymentRepository
import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.shared.exception.BusinessRuleException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class PaymentPortImpl(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val stripeGateway: PaymentGatewayPort,
) : PaymentPort {

    @Transactional
    override fun initiate(orderId: UUID, gateway: String): Payment {
        val order = orderRepository.findById(orderId)
            ?: throw ResourceNotFoundException("Order $orderId not found")
        if (order.status != OrderStatus.PENDING)
            throw BusinessRuleException("Order $orderId is not in PENDING status")

        val idempotencyKey = UUID.randomUUID()
        val existing = paymentRepository.findByIdempotencyKey(idempotencyKey)
        if (existing != null) return existing

        val now = Instant.now()
        val payment = paymentRepository.save(
            Payment(
                id = UUID.randomUUID(),
                orderId = orderId,
                gateway = gateway,
                gatewayReference = null,
                idempotencyKey = idempotencyKey,
                amount = order.total,
                status = PaymentStatus.PENDING,
                createdAt = now,
                updatedAt = now,
            )
        )
        stripeGateway.initiatePayment(orderId.toString(), order.total.amount, order.total.currency, idempotencyKey.toString())
        return payment
    }

    @Transactional
    override fun handleStripeWebhook(payload: String, signature: String) =
        handleWebhook(payload, signature, stripeGateway)

    @Transactional
    override fun handlePayPalWebhook(payload: String, signature: String) =
        handleWebhook(payload, signature, stripeGateway) // replaced by PayPalGatewayAdapter in prod

    @Transactional
    override fun handleMercadoPagoWebhook(payload: String, signature: String) =
        handleWebhook(payload, signature, stripeGateway) // replaced by MercadoPagoGatewayAdapter in prod

    private fun handleWebhook(payload: String, signature: String, gateway: PaymentGatewayPort) {
        val event = gateway.parseWebhookEvent(payload, signature)

        // idempotency: skip if already processed
        if (event.gatewayReference.isNotBlank() &&
            paymentRepository.findByGatewayReference(event.gatewayReference) != null &&
            paymentRepository.findByGatewayReference(event.gatewayReference)!!.status != PaymentStatus.PENDING
        ) return

        val orderId = event.orderId?.let { UUID.fromString(it) } ?: return
        val payment = paymentRepository.findByOrderId(orderId) ?: return
        val now = Instant.now()

        val newPaymentStatus = when (event.status) {
            PaymentEventStatus.SUCCEEDED -> PaymentStatus.SUCCEEDED
            PaymentEventStatus.FAILED    -> PaymentStatus.FAILED
            PaymentEventStatus.REFUNDED  -> PaymentStatus.REFUNDED
        }
        paymentRepository.save(payment.copy(status = newPaymentStatus, gatewayReference = event.gatewayReference, updatedAt = now))

        if (event.status == PaymentEventStatus.SUCCEEDED) {
            val order = orderRepository.findById(orderId) ?: return
            orderRepository.save(order.copy(status = OrderStatus.PAID, paymentId = payment.id, updatedAt = now))
        }
    }

    override fun getByOrderId(orderId: UUID): Payment =
        paymentRepository.findByOrderId(orderId) ?: throw ResourceNotFoundException("Payment for order $orderId not found")

    @Transactional
    override fun initiateRefund(orderId: UUID): Payment {
        val payment = paymentRepository.findByOrderId(orderId)
            ?: throw ResourceNotFoundException("Payment for order $orderId not found")
        if (payment.status != PaymentStatus.SUCCEEDED)
            throw BusinessRuleException("Can only refund a SUCCEEDED payment")
        stripeGateway.initiateRefund(payment.gatewayReference!!, payment.amount.amount, payment.amount.currency)
        return paymentRepository.save(payment.copy(status = PaymentStatus.REFUNDED, updatedAt = Instant.now()))
    }
}
