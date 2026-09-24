package com.mycompany.webstore.infrastructure.rest.customers

import com.mycompany.webstore.application.port.`in`.CustomerPort
import com.mycompany.webstore.domain.model.Customer
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.net.URI
import java.time.Instant
import java.util.UUID

data class RegisterRequest(@field:Email @field:NotBlank val email: String, @field:NotBlank val fullName: String, @field:NotBlank val password: String)
data class CustomerResponse(val id: UUID, val email: String, val fullName: String, val active: Boolean, val createdAt: Instant)

@Tag(name = "Customers")
@Path("/api/customers")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CustomerResource(private val customerPort: CustomerPort) {

    @POST @Path("/register")
    fun register(@Valid request: RegisterRequest): Response {
        val customer = customerPort.register(request.email, request.fullName, request.password)
        return Response.created(URI.create("/api/customers/${customer.id}")).entity(customer.toResponse()).build()
    }
}

@Tag(name = "Admin - Customers")
@Path("/api/admin/customers")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AdminCustomerResource(private val customerPort: CustomerPort) {

    @GET @RolesAllowed("admin")
    fun list(@QueryParam("q") q: String?, @QueryParam("page") @DefaultValue("0") page: Int, @QueryParam("size") @DefaultValue("20") size: Int): List<CustomerResponse> =
        customerPort.findAll(q, page, size).map { it.toResponse() }

    @GET @Path("/{id}") @RolesAllowed("admin")
    fun getById(@PathParam("id") id: UUID): CustomerResponse = customerPort.findById(id).toResponse()

    @PATCH @Path("/{id}/deactivate") @RolesAllowed("admin")
    fun deactivate(@PathParam("id") id: UUID): CustomerResponse = customerPort.deactivate(id).toResponse()

    @PATCH @Path("/{id}/reactivate") @RolesAllowed("admin")
    fun reactivate(@PathParam("id") id: UUID): CustomerResponse = customerPort.reactivate(id).toResponse()
}

private fun Customer.toResponse() = CustomerResponse(id, email, fullName, active, createdAt)
