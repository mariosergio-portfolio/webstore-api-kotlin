package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.application.port.out.CategoryRepository
import com.mycompany.webstore.domain.model.Category
import com.mycompany.webstore.infrastructure.persistence.repository.CategoryPanacheRepository
import com.mycompany.webstore.infrastructure.persistence.repository.ProductPanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class CategoryPersistenceAdapterImpl(
    private val repo: CategoryPanacheRepository,
    private val productRepo: ProductPanacheRepository,
    private val mapper: CategoryPersistenceMapper,
) : CategoryRepository {

    override fun save(category: Category): Category {
        val entity = mapper.toJpa(category)
        repo.getEntityManager().merge(entity)
        return mapper.toDomain(entity)
    }

    override fun findById(id: UUID): Category? = repo.findById(id)?.let(mapper::toDomain)
    override fun findAll(): List<Category> = repo.listAll().map(mapper::toDomain)
    override fun existsBySlug(slug: String): Boolean = repo.count("slug", slug) > 0
    override fun existsBySlugAndIdNot(slug: String, id: UUID): Boolean = repo.count("slug = ?1 and id != ?2", slug, id) > 0
    override fun hasProducts(categoryId: UUID): Boolean = productRepo.count("categoryId", categoryId) > 0
    override fun deleteById(id: UUID) { repo.deleteById(id) }
}
