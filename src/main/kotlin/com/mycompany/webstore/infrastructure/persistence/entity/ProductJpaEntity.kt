package com.mycompany.webstore.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "product")
class ProductJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(unique = true, nullable = false) var sku: String = ""
    @Column(nullable = false) var name: String = ""
    @Column(columnDefinition = "TEXT") var description: String? = null
    @Column(nullable = false, precision = 19, scale = 4) var price: BigDecimal = BigDecimal.ZERO
    @Column(nullable = false, length = 3) var currency: String = "USD"
    @Column(name = "stock_quantity", nullable = false) var stockQuantity: Int = 0
    @Version @Column(nullable = false) var version: Long = 0
    @Column(name = "category_id") var categoryId: UUID? = null
    @Column(nullable = false, length = 20) var status: String = "DRAFT"
    @Column(name = "reorder_point", nullable = false) var reorderPoint: Int = 5
    @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now()
    @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now()

    @OneToMany(mappedBy = "productId", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var images: MutableList<ProductImageJpaEntity> = mutableListOf()
}

@Entity
@Table(name = "product_image")
class ProductImageJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "product_id", nullable = false) var productId: UUID = UUID.randomUUID()
    @Column(nullable = false, length = 500) var url: String = ""
    @Column(name = "sort_order", nullable = false) var sortOrder: Int = 0
}
