package com.mycompany.webstore.domain.model

import java.time.Instant
import java.util.UUID

enum class ProductStatus { ACTIVE, DRAFT, ARCHIVED }

data class Product(
    val id: UUID,
    val sku: String,
    val name: String,
    val description: String?,
    val price: Money,
    val stockQuantity: Int,
    val reorderPoint: Int = 5,
    val categoryId: UUID?,
    val imageUrls: List<String> = emptyList(),
    val status: ProductStatus,
    val version: Long = 0,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(price.amount > java.math.BigDecimal.ZERO) { "Price must be > 0" }
        require(stockQuantity >= 0) { "Stock quantity must be >= 0" }
    }
}
