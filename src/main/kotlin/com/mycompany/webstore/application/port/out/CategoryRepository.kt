package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.Category
import java.util.UUID

interface CategoryRepository {
    fun save(category: Category): Category
    fun findById(id: UUID): Category?
    fun findAll(): List<Category>
    fun existsBySlug(slug: String): Boolean
    fun existsBySlugAndIdNot(slug: String, id: UUID): Boolean
    fun hasProducts(categoryId: UUID): Boolean
    fun deleteById(id: UUID)
}
