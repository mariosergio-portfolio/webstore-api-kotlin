package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.application.port.out.PaymentRepository
import com.mycompany.webstore.domain.model.Payment
import com.mycompany.webstore.infrastructure.persistence.repository.PaymentPanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class PaymentPersistenceAdapterImpl(
    private val repo: PaymentPanacheRepository,
    private val mapper: PaymentPersistenceMapper,
) : PaymentRepository {

    override fun save(payment: Payment): Payment {
        val entity = mapper.toJpa(payment)
        repo.getEntityManager().merge(entity)
        return mapper.toDomain(entity)
    }

    override fun findByOrderId(orderId: UUID): Payment? =
        repo.find("orderId", orderId).firstResult()?.let(mapper::toDomain)

    override fun findByGatewayReference(gatewayReference: String): Payment? =
        repo.find("gatewayReference", gatewayReference).firstResult()?.let(mapper::toDomain)

    override fun findByIdempotencyKey(key: UUID): Payment? =
        repo.find("idempotencyKey", key).firstResult()?.let(mapper::toDomain)
}
