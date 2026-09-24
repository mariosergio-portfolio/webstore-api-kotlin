package com.mycompany.webstore.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "payment")
class PaymentJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "order_id", nullable = false, unique = true) var orderId: UUID = UUID.randomUUID()
    @Column(nullable = false, length = 50) var gateway: String = ""
    @Column(name = "gateway_reference", unique = true) var gatewayReference: String? = null
    @Column(name = "idempotency_key", nullable = false, unique = true) var idempotencyKey: UUID = UUID.randomUUID()
    @Column(nullable = false, precision = 19, scale = 4) var amount: BigDecimal = BigDecimal.ZERO
    @Column(nullable = false, length = 3) var currency: String = "USD"
    @Column(nullable = false, length = 20) var status: String = "PENDING"
    @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now()
    @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now()
}
