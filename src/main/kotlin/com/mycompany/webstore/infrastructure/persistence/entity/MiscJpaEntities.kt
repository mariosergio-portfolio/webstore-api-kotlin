package com.mycompany.webstore.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "coupon")
class CouponJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(unique = true, nullable = false, length = 50) var code: String = ""
    @Column(name = "discount_type", nullable = false, length = 20) var discountType: String = ""
    @Column(nullable = false, precision = 19, scale = 4) var value: BigDecimal = BigDecimal.ZERO
    @Column(name = "min_order_amount", precision = 19, scale = 4) var minOrderAmount: BigDecimal? = null
    @Column(name = "max_uses") var maxUses: Int? = null
    @Column(name = "used_count", nullable = false) var usedCount: Int = 0
    @Column(name = "expires_at") var expiresAt: Instant? = null
    @Column(nullable = false) var active: Boolean = true
}

@Entity
@Table(name = "shipping_method")
class ShippingMethodJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(nullable = false) var name: String = ""
    @Column(nullable = false, precision = 19, scale = 4) var cost: BigDecimal = BigDecimal.ZERO
    @Column(nullable = false, length = 3) var currency: String = "USD"
    @Column(name = "estimated_days", nullable = false) var estimatedDays: Int = 0
    @Column(nullable = false) var active: Boolean = true
}

@Entity
@Table(name = "inventory_audit_log")
class InventoryAuditLogJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "product_id", nullable = false) var productId: UUID = UUID.randomUUID()
    @Column(name = "change_type", nullable = false, length = 20) var changeType: String = ""
    @Column(nullable = false) var delta: Int = 0
    @Column var reason: String? = null
    @Column var actor: String? = null
    @Column(name = "order_id") var orderId: UUID? = null
    @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now()
}
