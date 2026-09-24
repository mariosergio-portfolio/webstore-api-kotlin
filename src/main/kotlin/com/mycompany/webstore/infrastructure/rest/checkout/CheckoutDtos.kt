package com.mycompany.webstore.infrastructure.rest.checkout

import com.mycompany.webstore.infrastructure.rest.catalog.MoneyDto
import com.mycompany.webstore.infrastructure.rest.orders.OrderResponse
import jakarta.validation.constraints.*
import java.util.UUID

data class AddressDto(
    @field:NotBlank val street: String,
    @field:NotBlank val city: String,
    @field:NotBlank val state: String,
    @field:NotBlank val postalCode: String,
    @field:NotBlank val country: String,
)

data class PlaceOrderRequest(
    @field:NotNull val cartId: UUID,
    @field:NotNull val shippingAddress: AddressDto,
    val billingAddress: AddressDto?,
    @field:NotNull val shippingMethodId: UUID,
    val couponCode: String?,
)
