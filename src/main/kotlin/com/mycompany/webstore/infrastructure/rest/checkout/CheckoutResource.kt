package com.mycompany.webstore.infrastructure.rest.checkout

import com.mycompany.webstore.application.port.`in`.CheckoutPort
import com.mycompany.webstore.domain.model.Address
import com.mycompany.webstore.infrastructure.rest.orders.toResponse
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.net.URI
import java.util.UUID

@Tag(name = "Checkout")
@Path("/api/orders")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CheckoutResource(
    private val checkoutPort: CheckoutPort,
    private val jwt: JsonWebToken,
) {
    @POST @RolesAllowed("user", "admin")
    fun placeOrder(@Valid request: PlaceOrderRequest): Response {
        val customerId = UUID.fromString(jwt.subject)
        val order = checkoutPort.placeOrder(
            cartId = request.cartId,
            customerId = customerId,
            shippingAddress = request.shippingAddress.toDomain(),
            billingAddress = request.billingAddress?.toDomain(),
            shippingMethodId = request.shippingMethodId,
            couponCode = request.couponCode,
        )
        return Response.created(URI.create("/api/orders/${order.id}")).entity(order.toResponse()).build()
    }

    private fun AddressDto.toDomain() = Address(street, city, state, postalCode, country)
}
