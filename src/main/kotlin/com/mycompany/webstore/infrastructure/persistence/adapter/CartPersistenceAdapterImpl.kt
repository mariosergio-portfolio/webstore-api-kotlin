package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.application.port.out.CartRepository
import com.mycompany.webstore.domain.model.Cart
import com.mycompany.webstore.infrastructure.persistence.repository.CartPanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class CartPersistenceAdapterImpl(
    private val repo: CartPanacheRepository,
    private val mapper: CartPersistenceMapper,
) : CartRepository {

    override fun save(cart: Cart): Cart {
        val entity = mapper.toJpa(cart)
        repo.getEntityManager().merge(entity)
        return mapper.toDomain(entity)
    }

    override fun findByCustomerId(customerId: UUID): Cart? =
        repo.find("customerId", customerId).firstResult()?.let(mapper::toDomain)

    override fun findBySessionId(sessionId: String): Cart? =
        repo.find("sessionId", sessionId).firstResult()?.let(mapper::toDomain)

    override fun findById(id: UUID): Cart? = repo.findById(id)?.let(mapper::toDomain)

    override fun deleteById(id: UUID) { repo.deleteById(id) }
}
