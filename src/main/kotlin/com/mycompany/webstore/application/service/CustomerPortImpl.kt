package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.CustomerPort
import com.mycompany.webstore.application.port.out.CustomerRepository
import com.mycompany.webstore.domain.model.Customer
import com.mycompany.webstore.shared.exception.BusinessRuleException
import com.mycompany.webstore.shared.exception.DuplicateResourceException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class CustomerPortImpl(
    private val customerRepository: CustomerRepository,
) : CustomerPort {

    @Transactional
    override fun register(email: String, fullName: String, password: String): Customer {
        if (customerRepository.existsByEmail(email))
            throw DuplicateResourceException("Email '$email' is already registered")
        return customerRepository.save(
            Customer(UUID.randomUUID(), email, fullName, hashPassword(password), true, emptyList(), Instant.now())
        )
    }

    override fun findById(id: UUID): Customer =
        customerRepository.findById(id) ?: throw ResourceNotFoundException("Customer $id not found")

    override fun findAll(q: String?, page: Int, size: Int): List<Customer> =
        customerRepository.findAll(q, page, size)

    @Transactional
    override fun deactivate(id: UUID): Customer {
        val customer = findById(id)
        if (!customer.active) throw BusinessRuleException("Customer $id is already deactivated")
        return customerRepository.save(customer.copy(active = false))
    }

    @Transactional
    override fun reactivate(id: UUID): Customer {
        val customer = findById(id)
        if (customer.active) throw BusinessRuleException("Customer $id is already active")
        return customerRepository.save(customer.copy(active = true))
    }

    private fun hashPassword(password: String): String =
        org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt())
}
