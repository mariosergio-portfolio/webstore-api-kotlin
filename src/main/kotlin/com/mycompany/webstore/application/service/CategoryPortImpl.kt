package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.CategoryPort
import com.mycompany.webstore.application.port.out.CategoryRepository
import com.mycompany.webstore.domain.model.Category
import com.mycompany.webstore.shared.exception.BusinessRuleException
import com.mycompany.webstore.shared.exception.DuplicateResourceException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.UUID

@ApplicationScoped
class CategoryPortImpl(
    private val categoryRepository: CategoryRepository,
) : CategoryPort {

    @Transactional
    override fun create(category: Category): Category {
        if (categoryRepository.existsBySlug(category.slug))
            throw DuplicateResourceException("Slug '${category.slug}' already exists")
        category.parentId?.let {
            categoryRepository.findById(it) ?: throw ResourceNotFoundException("Parent category $it not found")
        }
        return categoryRepository.save(category)
    }

    override fun findById(id: UUID): Category =
        categoryRepository.findById(id) ?: throw ResourceNotFoundException("Category $id not found")

    override fun findAll(): List<Category> = categoryRepository.findAll()

    @Transactional
    override fun update(id: UUID, category: Category): Category {
        findById(id)
        if (categoryRepository.existsBySlugAndIdNot(category.slug, id))
            throw DuplicateResourceException("Slug '${category.slug}' is taken by another category")
        return categoryRepository.save(category.copy(id = id))
    }

    @Transactional
    override fun delete(id: UUID) {
        findById(id)
        if (categoryRepository.hasProducts(id))
            throw BusinessRuleException("Cannot delete category $id: it has products assigned")
        categoryRepository.deleteById(id)
    }
}
