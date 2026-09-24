package com.mycompany.webstore.infrastructure.rest.catalog

import com.mycompany.webstore.application.port.`in`.CategoryPort
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.net.URI
import java.util.UUID

@Tag(name = "Catalog - Categories")
@Path("/api/categories")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CategoryResource(
    private val categoryPort: CategoryPort,
    private val mapper: CategoryRestMapper,
) {
    @GET fun list(): List<CategoryResponse> = categoryPort.findAll().map(mapper::toResponse)
    @GET @Path("/{id}") fun getById(@PathParam("id") id: UUID): CategoryResponse = mapper.toResponse(categoryPort.findById(id))

    @POST @RolesAllowed("admin")
    fun create(@Valid request: CategoryRequest): Response {
        val created = categoryPort.create(mapper.toDomain(request))
        return Response.created(URI.create("/api/categories/${created.id}")).entity(mapper.toResponse(created)).build()
    }

    @PUT @Path("/{id}") @RolesAllowed("admin")
    fun update(@PathParam("id") id: UUID, @Valid request: CategoryRequest): CategoryResponse =
        mapper.toResponse(categoryPort.update(id, mapper.toDomain(request)))

    @DELETE @Path("/{id}") @RolesAllowed("admin")
    fun delete(@PathParam("id") id: UUID): Response { categoryPort.delete(id); return Response.noContent().build() }
}
