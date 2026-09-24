package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.Cart
import java.util.UUID

interface CartRepository {
    fun save(cart: Cart): Cart
    fun findByCustomerId(customerId: UUID): Cart?
    fun findBySessionId(sessionId: String): Cart?
    fun findById(id: UUID): Cart?
    fun deleteById(id: UUID)
}
