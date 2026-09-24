package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Cart
import java.util.UUID

interface CartPort {
    fun getOrCreate(customerId: UUID?, sessionId: String?): Cart
    fun addItem(cartId: UUID, productId: UUID, quantity: Int): Cart
    fun updateItemQuantity(cartId: UUID, itemId: UUID, quantity: Int): Cart
    fun removeItem(cartId: UUID, itemId: UUID): Cart
    fun applyCoupon(cartId: UUID, couponCode: String): Cart
    fun removeCoupon(cartId: UUID): Cart
    fun refreshPrices(cartId: UUID): Cart
    fun mergeAnonymousCart(customerId: UUID, sessionId: String): Cart
    fun clear(cartId: UUID)
}
