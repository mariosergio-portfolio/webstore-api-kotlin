package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.InventoryAuditLog
import com.mycompany.webstore.domain.model.Product
import java.util.UUID

interface InventoryPort {
    fun getStockLevel(productId: UUID): Int
    fun adjustStock(productId: UUID, delta: Int, reason: String, actor: String): Product
    fun getAuditLog(productId: UUID, page: Int, size: Int): List<InventoryAuditLog>
    fun listAll(lowStockOnly: Boolean, page: Int, size: Int): List<Product>
}
