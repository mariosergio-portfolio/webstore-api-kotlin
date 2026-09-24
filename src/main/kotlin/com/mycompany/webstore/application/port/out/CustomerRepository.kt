package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.Customer
import java.util.UUID

interface CustomerRepository {
    fun save(customer: Customer): Customer
    fun findById(id: UUID): Customer?
    fun findByEmail(email: String): Customer?
    fun findAll(q: String?, page: Int, size: Int): List<Customer>
    fun existsByEmail(email: String): Boolean
}
