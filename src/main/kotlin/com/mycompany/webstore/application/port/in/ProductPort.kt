package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Product
import java.util.UUID

interface ProductPort {
    fun create(product: Product): Product
    fun findById(id: UUID): Product
    fun findAll(page: Int, size: Int, categoryId: UUID?, q: String?, status: String?): List<Product>
    fun update(id: UUID, product: Product): Product
    fun archive(id: UUID)
    fun restore(id: UUID)
    fun count(categoryId: UUID?, q: String?, status: String?): Long
}
