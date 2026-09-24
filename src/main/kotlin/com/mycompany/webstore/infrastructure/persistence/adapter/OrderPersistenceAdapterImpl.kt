package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.application.port.out.OrderRepository
import com.mycompany.webstore.domain.model.Order
import com.mycompany.webstore.domain.model.OrderStatus
import com.mycompany.webstore.infrastructure.persistence.repository.OrderPanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class OrderPersistenceAdapterImpl(
    private val repo: OrderPanacheRepository,
    private val mapper: OrderPersistenceMapper,
) : OrderRepository {

    override fun save(order: Order): Order {
        val entity = mapper.toJpa(order)
        repo.getEntityManager().merge(entity)
        return mapper.toDomain(entity)
    }

    override fun findById(id: UUID): Order? = repo.findById(id)?.let(mapper::toDomain)

    override fun findByCustomerId(customerId: UUID, page: Int, size: Int): List<Order> =
        repo.find("customerId = ?1 order by placedAt desc", customerId).page(page, size).list().map(mapper::toDomain)

    override fun findAll(page: Int, size: Int, status: OrderStatus?, customerId: UUID?): List<Order> {
        var query = "1=1"
        val params = mutableMapOf<String, Any>()
        status?.let { query += " and status = :status"; params["status"] = it.name }
        customerId?.let { query += " and customerId = :customerId"; params["customerId"] = it }
        query += " order by placedAt desc"
        return repo.find(query, params).page(page, size).list().map(mapper::toDomain)
    }
}
