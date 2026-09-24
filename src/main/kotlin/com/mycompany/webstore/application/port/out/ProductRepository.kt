package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.Product
import com.mycompany.webstore.domain.model.ProductStatus
import java.util.UUID

interface ProductRepository {
    fun save(product: Product): Product
    fun findById(id: UUID): Product?
    fun findAll(page: Int, size: Int, categoryId: UUID?, q: String?, status: ProductStatus?): List<Product>
    fun count(categoryId: UUID?, q: String?, status: ProductStatus?): Long
    fun existsBySku(sku: String): Boolean
    fun existsBySkuAndIdNot(sku: String, id: UUID): Boolean
}
