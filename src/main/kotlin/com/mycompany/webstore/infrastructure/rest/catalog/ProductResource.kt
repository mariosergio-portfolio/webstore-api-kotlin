package com.mycompany.webstore.infrastructure.rest.catalog

import com.mycompany.webstore.application.port.`in`.ProductPort
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.net.URI
import java.util.UUID

@Tag(name = "Catalog - Products")
@Path("/api/products")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProductResource(
    private val productPort: ProductPort,
    private val mapper: ProductRestMapper,
) {
    @GET
    fun list(
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
        @QueryParam("categoryId") categoryId: UUID?,
        @QueryParam("q") q: String?,
        @QueryParam("status") status: String?,
    ): Response {
        val items = productPort.findAll(page, size, categoryId, q, status ?: "ACTIVE").map(mapper::toResponse)
        val total = productPort.count(categoryId, q, status ?: "ACTIVE")
        return Response.ok(items).header("X-Total-Count", total).build()
    }

    @GET @Path("/{id}")
    fun getById(@PathParam("id") id: UUID): ProductResponse = mapper.toResponse(productPort.findById(id))

    @POST @RolesAllowed("admin")
    fun create(@Valid request: ProductRequest): Response {
        val created = productPort.create(mapper.toDomain(request))
        return Response.created(URI.create("/api/products/${created.id}")).entity(mapper.toResponse(created)).build()
    }

    @PUT @Path("/{id}") @RolesAllowed("admin")
    fun update(@PathParam("id") id: UUID, @Valid request: ProductRequest): ProductResponse =
        mapper.toResponse(productPort.update(id, mapper.toDomain(request)))

    @DELETE @Path("/{id}") @RolesAllowed("admin")
    fun archive(@PathParam("id") id: UUID): Response { productPort.archive(id); return Response.noContent().build() }

    @PATCH @Path("/{id}/restore") @RolesAllowed("admin")
    fun restore(@PathParam("id") id: UUID): Response { productPort.restore(id); return Response.noContent().build() }
}
