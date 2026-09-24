package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.InventoryPort
import com.mycompany.webstore.application.port.out.InventoryAuditRepository
import com.mycompany.webstore.application.port.out.ProductRepository
import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.shared.exception.BusinessRuleException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class InventoryPortImpl(
    private val productRepository: ProductRepository,
    private val inventoryAuditRepository: InventoryAuditRepository,
) : InventoryPort {

    override fun getStockLevel(productId: UUID): Int =
        (productRepository.findById(productId) ?: throw ResourceNotFoundException("Product $productId not found")).stockQuantity

    @Transactional
    override fun adjustStock(productId: UUID, delta: Int, reason: String, actor: String): Product {
        val product = productRepository.findById(productId)
            ?: throw ResourceNotFoundException("Product $productId not found")
        val newQty = product.stockQuantity + delta
        if (newQty < 0) throw BusinessRuleException("Adjustment would result in negative stock")
        val updated = productRepository.save(product.copy(stockQuantity = newQty))
        inventoryAuditRepository.save(
            InventoryAuditLog(UUID.randomUUID(), productId, InventoryChangeType.ADJUSTMENT, delta, reason, actor, null, Instant.now())
        )
        return updated
    }

    override fun getAuditLog(productId: UUID, page: Int, size: Int): List<InventoryAuditLog> =
        inventoryAuditRepository.findByProductId(productId, page, size)

    override fun listAll(lowStockOnly: Boolean, page: Int, size: Int): List<Product> {
        val all = productRepository.findAll(page, size, null, null, null)
        return if (lowStockOnly) all.filter { it.stockQuantity < it.reorderPoint } else all
    }
}
