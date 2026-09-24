package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.Order
import com.mycompany.webstore.domain.model.OrderStatus
import java.util.UUID

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: UUID): Order?
    fun findByCustomerId(customerId: UUID, page: Int, size: Int): List<Order>
    fun findAll(page: Int, size: Int, status: OrderStatus?, customerId: UUID?): List<Order>
}
