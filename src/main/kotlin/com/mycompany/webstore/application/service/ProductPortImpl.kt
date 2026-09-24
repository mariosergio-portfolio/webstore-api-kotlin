package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.ProductPort
import com.mycompany.webstore.application.port.out.CategoryRepository
import com.mycompany.webstore.application.port.out.ProductRepository
import com.mycompany.webstore.domain.model.Product
import com.mycompany.webstore.domain.model.ProductStatus
import com.mycompany.webstore.shared.exception.BusinessRuleException
import com.mycompany.webstore.shared.exception.DuplicateResourceException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.UUID

@ApplicationScoped
class ProductPortImpl(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
) : ProductPort {

    @Transactional
    override fun create(product: Product): Product {
        if (productRepository.existsBySku(product.sku))
            throw DuplicateResourceException("SKU '${product.sku}' already exists")
        product.categoryId?.let {
            categoryRepository.findById(it) ?: throw ResourceNotFoundException("Category $it not found")
        }
        return productRepository.save(product)
    }

    override fun findById(id: UUID): Product =
        productRepository.findById(id) ?: throw ResourceNotFoundException("Product $id not found")

    override fun findAll(page: Int, size: Int, categoryId: UUID?, q: String?, status: String?): List<Product> {
        val parsedStatus = status?.let { ProductStatus.valueOf(it) }
        return productRepository.findAll(page, size, categoryId, q, parsedStatus)
    }

    override fun count(categoryId: UUID?, q: String?, status: String?): Long {
        val parsedStatus = status?.let { ProductStatus.valueOf(it) }
        return productRepository.count(categoryId, q, parsedStatus)
    }

    @Transactional
    override fun update(id: UUID, product: Product): Product {
        val existing = findById(id)
        if (productRepository.existsBySkuAndIdNot(product.sku, id))
            throw DuplicateResourceException("SKU '${product.sku}' is taken by another product")
        product.categoryId?.let {
            categoryRepository.findById(it) ?: throw ResourceNotFoundException("Category $it not found")
        }
        return productRepository.save(product.copy(id = id, version = existing.version, createdAt = existing.createdAt))
    }

    @Transactional
    override fun archive(id: UUID) {
        val existing = findById(id)
        productRepository.save(existing.copy(status = ProductStatus.ARCHIVED))
    }

    @Transactional
    override fun restore(id: UUID) {
        val existing = findById(id)
        if (existing.status != ProductStatus.ARCHIVED)
            throw BusinessRuleException("Product $id is not archived")
        productRepository.save(existing.copy(status = ProductStatus.ACTIVE))
    }
}
