package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.CheckoutPort
import com.mycompany.webstore.application.port.out.*
import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.domain.service.OrderTotalCalculator
import com.mycompany.webstore.shared.exception.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class CheckoutPortImpl(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val shippingMethodRepository: ShippingMethodRepository,
    private val inventoryAuditRepository: InventoryAuditRepository,
) : CheckoutPort {

    @Transactional
    override fun placeOrder(
        cartId: UUID,
        customerId: UUID,
        shippingAddress: Address,
        billingAddress: Address?,
        shippingMethodId: UUID,
        couponCode: String?,
    ): Order {
        val cart = cartRepository.findById(cartId)
            ?: throw ResourceNotFoundException("Cart $cartId not found")
        if (cart.isEmpty) throw BusinessRuleException("Cannot place order from an empty cart")

        val shippingMethod = shippingMethodRepository.findById(shippingMethodId)
            ?: throw ResourceNotFoundException("Shipping method $shippingMethodId not found")

        // Stock check + deduction
        val stockConflicts = mutableListOf<StockConflict>()
        val savedProducts = cart.items.map { item ->
            val product = productRepository.findById(item.productId)
                ?: throw ResourceNotFoundException("Product ${item.productId} not found")
            if (product.stockQuantity < item.quantity)
                stockConflicts.add(StockConflict(product.id, item.quantity, product.stockQuantity))
            product
        }
        if (stockConflicts.isNotEmpty())
            throw InsufficientStockException(stockConflicts, "Some items are out of stock")

        val coupon = couponCode?.let { couponRepository.findByCode(it) }

        val totals = OrderTotalCalculator.calculate(
            cart.items, coupon, shippingMethod.cost, shippingMethod.cost.currency
        )

        val orderId = UUID.randomUUID()
        val now = Instant.now()

        val orderItems = cart.items.mapIndexed { _, item ->
            OrderItem(
                id = UUID.randomUUID(),
                orderId = orderId,
                productId = item.productId,
                sku = savedProducts.first { it.id == item.productId }.sku,
                name = savedProducts.first { it.id == item.productId }.name,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                lineTotal = item.unitPrice * item.quantity,
            )
        }

        val order = orderRepository.save(
            Order(
                id = orderId,
                customerId = customerId,
                status = OrderStatus.PENDING,
                items = orderItems,
                subtotal = totals.subtotal,
                discountAmount = totals.discountAmount,
                shippingCost = totals.shippingCost,
                total = totals.total,
                shippingAddress = shippingAddress,
                billingAddress = billingAddress,
                shippingMethodId = shippingMethodId,
                trackingCarrier = null,
                trackingNumber = null,
                paymentId = null,
                placedAt = now,
                updatedAt = now,
            )
        )

        // Deduct stock + audit
        cart.items.forEach { item ->
            val product = savedProducts.first { it.id == item.productId }
            productRepository.save(product.copy(stockQuantity = product.stockQuantity - item.quantity))
            inventoryAuditRepository.save(
                InventoryAuditLog(
                    id = UUID.randomUUID(),
                    productId = item.productId,
                    changeType = InventoryChangeType.DEDUCTION,
                    delta = -item.quantity,
                    reason = "Order placed",
                    actor = "system",
                    orderId = orderId,
                    createdAt = now,
                )
            )
        }

        // Increment coupon usage
        coupon?.let { couponRepository.save(it.copy(usedCount = it.usedCount + 1)) }

        // Clear cart
        cartRepository.save(cart.copy(items = emptyList(), couponId = null, updatedAt = now))

        return order
    }
}
