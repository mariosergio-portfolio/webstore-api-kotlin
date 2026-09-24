package com.mycompany.webstore.infrastructure.rest.catalog

import com.mycompany.webstore.domain.model.ProductStatus
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class MoneyDto(val amount: BigDecimal, val currency: String)

data class ProductRequest(
    @field:NotBlank val sku: String,
    @field:NotBlank val name: String,
    val description: String?,
    @field:NotNull val price: MoneyDto,
    @field:Min(0) val stockQuantity: Int = 0,
    val categoryId: UUID?,
    val imageUrls: List<String> = emptyList(),
    val status: ProductStatus = ProductStatus.DRAFT,
)

data class ProductResponse(
    val id: UUID,
    val sku: String,
    val name: String,
    val description: String?,
    val price: MoneyDto,
    val stockQuantity: Int,
    val categoryId: UUID?,
    val imageUrls: List<String>,
    val status: ProductStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class CategoryRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val slug: String,
    val parentId: UUID?,
    val sortOrder: Int = 0,
)

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val slug: String,
    val parentId: UUID?,
    val sortOrder: Int,
)
