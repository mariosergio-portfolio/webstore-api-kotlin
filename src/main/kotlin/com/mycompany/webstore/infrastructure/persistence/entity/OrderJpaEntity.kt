package com.mycompany.webstore.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "\"order\"")
class OrderJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "customer_id", nullable = false) var customerId: UUID = UUID.randomUUID()
    @Column(nullable = false, length = 20) var status: String = "PENDING"
    @Column(nullable = false, precision = 19, scale = 4) var subtotal: BigDecimal = BigDecimal.ZERO
    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 4) var discountAmount: BigDecimal = BigDecimal.ZERO
    @Column(name = "shipping_cost", nullable = false, precision = 19, scale = 4) var shippingCost: BigDecimal = BigDecimal.ZERO
    @Column(nullable = false, precision = 19, scale = 4) var total: BigDecimal = BigDecimal.ZERO
    @Column(nullable = false, length = 3) var currency: String = "USD"
    @Column(name = "shipping_street", nullable = false) var shippingStreet: String = ""
    @Column(name = "shipping_city", nullable = false) var shippingCity: String = ""
    @Column(name = "shipping_state", nullable = false) var shippingState: String = ""
    @Column(name = "shipping_postal_code", nullable = false) var shippingPostalCode: String = ""
    @Column(name = "shipping_country", nullable = false) var shippingCountry: String = ""
    @Column(name = "billing_street") var billingStreet: String? = null
    @Column(name = "billing_city") var billingCity: String? = null
    @Column(name = "billing_state") var billingState: String? = null
    @Column(name = "billing_postal_code") var billingPostalCode: String? = null
    @Column(name = "billing_country") var billingCountry: String? = null
    @Column(name = "shipping_method_id") var shippingMethodId: UUID? = null
    @Column(name = "tracking_carrier") var trackingCarrier: String? = null
    @Column(name = "tracking_number") var trackingNumber: String? = null
    @Column(name = "payment_id") var paymentId: UUID? = null
    @Column(name = "placed_at", nullable = false) var placedAt: Instant = Instant.now()
    @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now()

    @OneToMany(mappedBy = "orderId", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var items: MutableList<OrderItemJpaEntity> = mutableListOf()
}

@Entity
@Table(name = "order_item")
class OrderItemJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "order_id", nullable = false) var orderId: UUID = UUID.randomUUID()
    @Column(name = "product_id", nullable = false) var productId: UUID = UUID.randomUUID()
    @Column(nullable = false) var sku: String = ""
    @Column(nullable = false) var name: String = ""
    @Column(nullable = false) var quantity: Int = 1
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4) var unitPrice: BigDecimal = BigDecimal.ZERO
    @Column(name = "unit_price_currency", nullable = false, length = 3) var unitPriceCurrency: String = "USD"
    @Column(name = "line_total", nullable = false, precision = 19, scale = 4) var lineTotal: BigDecimal = BigDecimal.ZERO
}
