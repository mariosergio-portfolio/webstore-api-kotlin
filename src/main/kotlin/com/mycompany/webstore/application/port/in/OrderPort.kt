package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Order
import com.mycompany.webstore.domain.model.OrderStatus
import java.util.UUID

interface OrderPort {
    fun findById(id: UUID): Order
    fun findByCustomerId(customerId: UUID, page: Int, size: Int): List<Order>
    fun findAll(page: Int, size: Int, status: OrderStatus?, customerId: UUID?): List<Order>
    fun cancelByCustomer(orderId: UUID, customerId: UUID): Order
    fun advanceStatus(orderId: UUID, newStatus: OrderStatus, trackingCarrier: String?, trackingNumber: String?): Order
    fun adminCancel(orderId: UUID): Order
}
