package com.mycompany.webstore.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "cart")
class CartJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "customer_id", unique = true) var customerId: UUID? = null
    @Column(name = "session_id", unique = true) var sessionId: String? = null
    @Column(name = "coupon_id") var couponId: UUID? = null
    @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now()

    @OneToMany(mappedBy = "cartId", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var items: MutableList<CartItemJpaEntity> = mutableListOf()
}

@Entity
@Table(name = "cart_item", uniqueConstraints = [UniqueConstraint(columnNames = ["cart_id", "product_id"])])
class CartItemJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "cart_id", nullable = false) var cartId: UUID = UUID.randomUUID()
    @Column(name = "product_id", nullable = false) var productId: UUID = UUID.randomUUID()
    @Column(nullable = false) var quantity: Int = 1
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4) var unitPrice: BigDecimal = BigDecimal.ZERO
    @Column(name = "unit_price_currency", nullable = false, length = 3) var unitPriceCurrency: String = "USD"
}
