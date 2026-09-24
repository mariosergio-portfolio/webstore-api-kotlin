package com.mycompany.webstore.infrastructure.rest.cart

import com.mycompany.webstore.application.port.`in`.CartPort
import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.UUID

@Tag(name = "Cart")
@Path("/api/cart")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CartResource(
    private val cartPort: CartPort,
    private val jwt: JsonWebToken,
) {
    @GET @PermitAll
    fun getCart(@CookieParam("sessionId") sessionId: String?): CartResponse {
        val cart = cartPort.getOrCreate(customerId(), sessionId)
        return cart.toResponse()
    }

    @POST @Path("/items") @PermitAll
    fun addItem(@CookieParam("sessionId") sessionId: String?, @Valid request: AddItemRequest): CartResponse {
        val cart = cartPort.getOrCreate(customerId(), sessionId)
        return cartPort.addItem(cart.id, request.productId, request.quantity).toResponse()
    }

    @PATCH @Path("/items/{itemId}") @PermitAll
    fun updateItem(@PathParam("itemId") itemId: UUID, @CookieParam("sessionId") sessionId: String?, @Valid request: UpdateItemRequest): CartResponse {
        val cart = cartPort.getOrCreate(customerId(), sessionId)
        return cartPort.updateItemQuantity(cart.id, itemId, request.quantity).toResponse()
    }

    @DELETE @Path("/items/{itemId}") @PermitAll
    fun removeItem(@PathParam("itemId") itemId: UUID, @CookieParam("sessionId") sessionId: String?): CartResponse {
        val cart = cartPort.getOrCreate(customerId(), sessionId)
        return cartPort.removeItem(cart.id, itemId).toResponse()
    }

    @POST @Path("/coupon") @PermitAll
    fun applyCoupon(@CookieParam("sessionId") sessionId: String?, @Valid request: ApplyCouponRequest): CartResponse {
        val cart = cartPort.getOrCreate(customerId(), sessionId)
        return cartPort.applyCoupon(cart.id, request.couponCode).toResponse()
    }

    @DELETE @Path("/coupon") @PermitAll
    fun removeCoupon(@CookieParam("sessionId") sessionId: String?): CartResponse {
        val cart = cartPort.getOrCreate(customerId(), sessionId)
        return cartPort.removeCoupon(cart.id).toResponse()
    }

    @POST @Path("/refresh") @PermitAll
    fun refresh(@CookieParam("sessionId") sessionId: String?): CartResponse {
        val cart = cartPort.getOrCreate(customerId(), sessionId)
        return cartPort.refreshPrices(cart.id).toResponse()
    }

    private fun customerId(): UUID? = try { UUID.fromString(jwt.subject) } catch (e: Exception) { null }

    private fun com.mycompany.webstore.domain.model.Cart.toResponse() = CartResponse(
        id = id,
        items = items.map { CartItemResponse(it.id, it.productId, it.quantity, it.unitPrice.amount, it.unitPrice.currency, it.unitPrice.amount * java.math.BigDecimal(it.quantity)) },
        couponId = couponId,
        updatedAt = updatedAt,
    )
}
