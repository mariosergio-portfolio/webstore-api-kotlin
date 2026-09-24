package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.application.port.out.CustomerRepository
import com.mycompany.webstore.domain.model.Customer
import com.mycompany.webstore.infrastructure.persistence.repository.CustomerPanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class CustomerPersistenceAdapterImpl(
    private val repo: CustomerPanacheRepository,
    private val mapper: CustomerPersistenceMapper,
) : CustomerRepository {

    override fun save(customer: Customer): Customer {
        val entity = mapper.toJpa(customer)
        repo.getEntityManager().merge(entity)
        return mapper.toDomain(entity)
    }

    override fun findById(id: UUID): Customer? = repo.findById(id)?.let(mapper::toDomain)

    override fun findByEmail(email: String): Customer? =
        repo.find("email", email).firstResult()?.let(mapper::toDomain)

    override fun findAll(q: String?, page: Int, size: Int): List<Customer> {
        val query = if (q != null) "lower(email) like ?1 or lower(fullName) like ?1" else "1=1"
        val param = "%${q?.lowercase() ?: ""}%"
        return if (q != null)
            repo.find(query, param).page(page, size).list().map(mapper::toDomain)
        else
            repo.findAll().page(page, size).list().map(mapper::toDomain)
    }

    override fun existsByEmail(email: String): Boolean = repo.count("email", email) > 0
}
