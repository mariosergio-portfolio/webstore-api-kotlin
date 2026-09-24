package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.application.port.out.ProductRepository
import com.mycompany.webstore.domain.model.Product
import com.mycompany.webstore.domain.model.ProductStatus
import com.mycompany.webstore.infrastructure.persistence.repository.ProductPanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class ProductPersistenceAdapterImpl(
    private val repo: ProductPanacheRepository,
    private val mapper: ProductPersistenceMapper,
) : ProductRepository {

    override fun save(product: Product): Product {
        val entity = mapper.toJpa(product)
        repo.getEntityManager().merge(entity)
        return mapper.toDomain(entity)
    }

    override fun findById(id: UUID): Product? = repo.findById(id)?.let(mapper::toDomain)

    override fun findAll(page: Int, size: Int, categoryId: UUID?, q: String?, status: ProductStatus?): List<Product> {
        var query = "1=1"
        val params = mutableMapOf<String, Any>()
        categoryId?.let { query += " and categoryId = :categoryId"; params["categoryId"] = it }
        q?.let { query += " and (lower(name) like :q or lower(description) like :q)"; params["q"] = "%${it.lowercase()}%" }
        status?.let { query += " and status = :status"; params["status"] = it.name }
        return repo.find(query, params).page(page, size).list().map(mapper::toDomain)
    }

    override fun count(categoryId: UUID?, q: String?, status: ProductStatus?): Long {
        var query = "1=1"
        val params = mutableMapOf<String, Any>()
        categoryId?.let { query += " and categoryId = :categoryId"; params["categoryId"] = it }
        q?.let { query += " and (lower(name) like :q or lower(description) like :q)"; params["q"] = "%${it.lowercase()}%" }
        status?.let { query += " and status = :status"; params["status"] = it.name }
        return repo.count(query, params)
    }

    override fun existsBySku(sku: String): Boolean = repo.count("sku", sku) > 0

    override fun existsBySkuAndIdNot(sku: String, id: UUID): Boolean =
        repo.count("sku = ?1 and id != ?2", sku, id) > 0
}
