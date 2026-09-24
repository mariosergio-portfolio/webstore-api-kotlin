package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.InventoryAuditLog
import java.util.UUID

interface InventoryAuditRepository {
    fun save(log: InventoryAuditLog): InventoryAuditLog
    fun findByProductId(productId: UUID, page: Int, size: Int): List<InventoryAuditLog>
}
