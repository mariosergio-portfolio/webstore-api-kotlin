package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Address
import com.mycompany.webstore.domain.model.Order
import java.util.UUID

interface CheckoutPort {
    fun placeOrder(
        cartId: UUID,
        customerId: UUID,
        shippingAddress: Address,
        billingAddress: Address?,
        shippingMethodId: UUID,
        couponCode: String?,
    ): Order
}
