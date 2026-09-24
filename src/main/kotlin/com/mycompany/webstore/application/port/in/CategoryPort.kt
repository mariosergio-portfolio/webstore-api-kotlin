package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Category
import java.util.UUID

interface CategoryPort {
    fun create(category: Category): Category
    fun findById(id: UUID): Category
    fun findAll(): List<Category>
    fun update(id: UUID, category: Category): Category
    fun delete(id: UUID)
}
