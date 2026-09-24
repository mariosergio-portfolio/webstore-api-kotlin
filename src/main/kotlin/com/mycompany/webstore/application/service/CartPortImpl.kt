package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.CartPort
import com.mycompany.webstore.application.port.out.CartRepository
import com.mycompany.webstore.application.port.out.CouponRepository
import com.mycompany.webstore.application.port.out.ProductRepository
import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.shared.exception.BusinessRuleException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class CartPortImpl(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val couponRepository: CouponRepository,
) : CartPort {

    @Transactional
    override fun getOrCreate(customerId: UUID?, sessionId: String?): Cart {
        val existing = when {
            customerId != null -> cartRepository.findByCustomerId(customerId)
            sessionId != null  -> cartRepository.findBySessionId(sessionId)
            else               -> throw BusinessRuleException("Either customerId or sessionId must be provided")
        }
        return existing ?: cartRepository.save(
            Cart(
                id = UUID.randomUUID(),
                customerId = customerId,
                sessionId = sessionId,
                items = emptyList(),
                couponId = null,
                updatedAt = Instant.now(),
            )
        )
    }

    @Transactional
    override fun addItem(cartId: UUID, productId: UUID, quantity: Int): Cart {
        val cart = getCart(cartId)
        val product = productRepository.findById(productId)
            ?: throw ResourceNotFoundException("Product $productId not found")
        if (product.status == ProductStatus.ARCHIVED)
            throw BusinessRuleException("Cannot add archived product to cart")
        if (product.stockQuantity < quantity)
            throw BusinessRuleException("Insufficient stock: available ${product.stockQuantity}")

        val existingItem = cart.items.find { it.productId == productId }
        val newItems = if (existingItem != null) {
            val newQty = existingItem.quantity + quantity
            if (product.stockQuantity < newQty)
                throw BusinessRuleException("Insufficient stock: available ${product.stockQuantity}")
            cart.items.map { if (it.productId == productId) it.copy(quantity = newQty) else it }
        } else {
            cart.items + CartItem(UUID.randomUUID(), cartId, productId, quantity, product.price)
        }
        return cartRepository.save(cart.copy(items = newItems, updatedAt = Instant.now()))
    }

    @Transactional
    override fun updateItemQuantity(cartId: UUID, itemId: UUID, quantity: Int): Cart {
        val cart = getCart(cartId)
        if (quantity == 0) return removeItem(cartId, itemId)
        val item = cart.items.find { it.id == itemId }
            ?: throw ResourceNotFoundException("Cart item $itemId not found")
        val product = productRepository.findById(item.productId)
            ?: throw ResourceNotFoundException("Product ${item.productId} not found")
        if (product.stockQuantity < quantity)
            throw BusinessRuleException("Insufficient stock: available ${product.stockQuantity}")
        val updated = cart.items.map { if (it.id == itemId) it.copy(quantity = quantity) else it }
        return cartRepository.save(cart.copy(items = updated, updatedAt = Instant.now()))
    }

    @Transactional
    override fun removeItem(cartId: UUID, itemId: UUID): Cart {
        val cart = getCart(cartId)
        return cartRepository.save(cart.copy(items = cart.items.filter { it.id != itemId }, updatedAt = Instant.now()))
    }

    @Transactional
    override fun applyCoupon(cartId: UUID, couponCode: String): Cart {
        val cart = getCart(cartId)
        val coupon = couponRepository.findByCode(couponCode)
            ?: throw ResourceNotFoundException("Coupon '$couponCode' not found")
        val subtotal = cart.items.fold(java.math.BigDecimal.ZERO) { acc, i -> acc + i.unitPrice.amount * java.math.BigDecimal(i.quantity) }
        if (!coupon.isValid(subtotal, Instant.now()))
            throw BusinessRuleException("Coupon '$couponCode' is not valid")
        return cartRepository.save(cart.copy(couponId = coupon.id, updatedAt = Instant.now()))
    }

    @Transactional
    override fun removeCoupon(cartId: UUID): Cart {
        val cart = getCart(cartId)
        return cartRepository.save(cart.copy(couponId = null, updatedAt = Instant.now()))
    }

    @Transactional
    override fun refreshPrices(cartId: UUID): Cart {
        val cart = getCart(cartId)
        val refreshed = cart.items.map { item ->
            val product = productRepository.findById(item.productId)
            if (product == null || product.status == ProductStatus.ARCHIVED || product.stockQuantity == 0) {
                item // client will see stale price and must resolve
            } else {
                item.copy(unitPrice = product.price)
            }
        }
        return cartRepository.save(cart.copy(items = refreshed, updatedAt = Instant.now()))
    }

    @Transactional
    override fun mergeAnonymousCart(customerId: UUID, sessionId: String): Cart {
        val anonCart = cartRepository.findBySessionId(sessionId) ?: return getOrCreate(customerId, null)
        val customerCart = cartRepository.findByCustomerId(customerId) ?: cartRepository.save(
            Cart(UUID.randomUUID(), customerId, null, emptyList(), null, Instant.now())
        )
        val mergedItems = anonCart.items.fold(customerCart.items.toMutableList()) { acc, anonItem ->
            val existing = acc.find { it.productId == anonItem.productId }
            if (existing != null) {
                acc.replaceAll { if (it.productId == anonItem.productId) it.copy(quantity = it.quantity + anonItem.quantity) else it }
            } else {
                acc.add(anonItem.copy(id = UUID.randomUUID(), cartId = customerCart.id))
            }
            acc
        }
        val merged = cartRepository.save(customerCart.copy(items = mergedItems, updatedAt = Instant.now()))
        cartRepository.deleteById(anonCart.id)
        return merged
    }

    @Transactional
    override fun clear(cartId: UUID) {
        val cart = getCart(cartId)
        cartRepository.save(cart.copy(items = emptyList(), couponId = null, updatedAt = Instant.now()))
    }

    private fun getCart(cartId: UUID): Cart =
        cartRepository.findById(cartId) ?: throw ResourceNotFoundException("Cart $cartId not found")
}
