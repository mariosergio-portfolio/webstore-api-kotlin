package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.OrderPort
import com.mycompany.webstore.application.port.out.InventoryAuditRepository
import com.mycompany.webstore.application.port.out.OrderRepository
import com.mycompany.webstore.application.port.out.ProductRepository
import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.shared.exception.BusinessRuleException
import com.mycompany.webstore.shared.exception.InvalidStatusTransitionException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class OrderPortImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val inventoryAuditRepository: InventoryAuditRepository,
) : OrderPort {

    override fun findById(id: UUID): Order =
        orderRepository.findById(id) ?: throw ResourceNotFoundException("Order $id not found")

    override fun findByCustomerId(customerId: UUID, page: Int, size: Int): List<Order> =
        orderRepository.findByCustomerId(customerId, page, size)

    override fun findAll(page: Int, size: Int, status: OrderStatus?, customerId: UUID?): List<Order> =
        orderRepository.findAll(page, size, status, customerId)

    @Transactional
    override fun cancelByCustomer(orderId: UUID, customerId: UUID): Order {
        val order = findById(orderId)
        if (order.customerId != customerId) throw BusinessRuleException("Order $orderId does not belong to customer $customerId")
        if (order.status != OrderStatus.PENDING)
            throw BusinessRuleException("Customer can only cancel orders in PENDING status")
        return cancelAndRestoreStock(order)
    }

    @Transactional
    override fun advanceStatus(orderId: UUID, newStatus: OrderStatus, trackingCarrier: String?, trackingNumber: String?): Order {
        val order = findById(orderId)
        if (!order.canTransitionTo(newStatus))
            throw InvalidStatusTransitionException("Cannot transition order from ${order.status} to $newStatus")
        return orderRepository.save(
            order.copy(
                status = newStatus,
                trackingCarrier = trackingCarrier ?: order.trackingCarrier,
                trackingNumber = trackingNumber ?: order.trackingNumber,
                updatedAt = Instant.now(),
            )
        )
    }

    @Transactional
    override fun adminCancel(orderId: UUID): Order {
        val order = findById(orderId)
        if (order.status !in Order.CANCELLABLE_STATUSES)
            throw BusinessRuleException("Order $orderId cannot be cancelled from status ${order.status}")
        return cancelAndRestoreStock(order)
    }

    private fun cancelAndRestoreStock(order: Order): Order {
        val now = Instant.now()
        order.items.forEach { item ->
            val product = productRepository.findById(item.productId)
            if (product != null) {
                productRepository.save(product.copy(stockQuantity = product.stockQuantity + item.quantity))
                inventoryAuditRepository.save(
                    InventoryAuditLog(
                        id = UUID.randomUUID(),
                        productId = item.productId,
                        changeType = InventoryChangeType.RESTORATION,
                        delta = item.quantity,
                        reason = "Order cancelled",
                        actor = "system",
                        orderId = order.id,
                        createdAt = now,
                    )
                )
            }
        }
        return orderRepository.save(order.copy(status = OrderStatus.CANCELLED, updatedAt = now))
    }
}
