package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.Payment
import java.util.UUID

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findByOrderId(orderId: UUID): Payment?
    fun findByGatewayReference(gatewayReference: String): Payment?
    fun findByIdempotencyKey(key: UUID): Payment?
}
