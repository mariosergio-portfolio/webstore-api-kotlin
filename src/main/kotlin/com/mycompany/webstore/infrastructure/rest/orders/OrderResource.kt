package com.mycompany.webstore.infrastructure.rest.orders

import com.mycompany.webstore.application.port.`in`.OrderPort
import com.mycompany.webstore.domain.model.OrderStatus
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.util.UUID

@Tag(name = "Orders")
@Path("/api/orders")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrderResource(
    private val orderPort: OrderPort,
    private val jwt: JsonWebToken,
) {
    @GET @RolesAllowed("user", "admin")
    fun list(@QueryParam("page") @DefaultValue("0") page: Int, @QueryParam("size") @DefaultValue("20") size: Int): List<OrderResponse> {
        val customerId = UUID.fromString(jwt.subject)
        return orderPort.findByCustomerId(customerId, page, size).map { it.toResponse() }
    }

    @GET @Path("/{id}") @RolesAllowed("user", "admin")
    fun getById(@PathParam("id") id: UUID): OrderResponse = orderPort.findById(id).toResponse()

    @DELETE @Path("/{id}") @RolesAllowed("user", "admin")
    fun cancel(@PathParam("id") id: UUID): OrderResponse {
        val customerId = UUID.fromString(jwt.subject)
        return orderPort.cancelByCustomer(id, customerId).toResponse()
    }
}

@Tag(name = "Admin - Orders")
@Path("/api/admin/orders")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AdminOrderResource(
    private val orderPort: OrderPort,
) {
    @GET @RolesAllowed("admin")
    fun list(
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
        @QueryParam("status") status: String?,
        @QueryParam("customerId") customerId: UUID?,
    ): List<OrderResponse> {
        val parsedStatus = status?.let { OrderStatus.valueOf(it) }
        return orderPort.findAll(page, size, parsedStatus, customerId).map { it.toResponse() }
    }

    @GET @Path("/{id}") @RolesAllowed("admin")
    fun getById(@PathParam("id") id: UUID): OrderResponse = orderPort.findById(id).toResponse()

    @PATCH @Path("/{id}/status") @RolesAllowed("admin")
    fun advance(@PathParam("id") id: UUID, request: AdvanceStatusRequest): OrderResponse =
        orderPort.advanceStatus(id, request.status, request.trackingCarrier, request.trackingNumber).toResponse()

    @DELETE @Path("/{id}") @RolesAllowed("admin")
    fun cancel(@PathParam("id") id: UUID): OrderResponse = orderPort.adminCancel(id).toResponse()
}
