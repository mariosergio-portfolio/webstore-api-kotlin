package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Customer
import java.util.UUID

interface CustomerPort {
    fun register(email: String, fullName: String, password: String): Customer
    fun findById(id: UUID): Customer
    fun findAll(q: String?, page: Int, size: Int): List<Customer>
    fun deactivate(id: UUID): Customer
    fun reactivate(id: UUID): Customer
}
