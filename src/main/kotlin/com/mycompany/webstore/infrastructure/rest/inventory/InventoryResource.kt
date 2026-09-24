package com.mycompany.webstore.infrastructure.rest.inventory

import com.mycompany.webstore.application.port.`in`.InventoryPort
import com.mycompany.webstore.domain.model.Product
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.util.UUID

data class StockAdjustRequest(@field:NotNull val delta: Int, @field:NotBlank val reason: String, @field:NotBlank val actor: String)
data class StockLevelResponse(val productId: UUID, val sku: String, val name: String, val stockQuantity: Int, val reorderPoint: Int)

@Tag(name = "Admin - Inventory")
@Path("/api/admin/inventory")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class InventoryResource(private val inventoryPort: InventoryPort) {

    @GET @RolesAllowed("admin")
    fun list(
        @QueryParam("lowStock") @DefaultValue("false") lowStock: Boolean,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ): List<StockLevelResponse> = inventoryPort.listAll(lowStock, page, size).map { it.toStockResponse() }

    @GET @Path("/{productId}") @RolesAllowed("admin")
    fun get(@PathParam("productId") productId: UUID): StockLevelResponse {
        val level = inventoryPort.getStockLevel(productId)
        return StockLevelResponse(productId, "", "", level, 5)
    }

    @PATCH @Path("/{productId}") @RolesAllowed("admin")
    fun adjust(@PathParam("productId") productId: UUID, @Valid request: StockAdjustRequest): StockLevelResponse =
        inventoryPort.adjustStock(productId, request.delta, request.reason, request.actor).toStockResponse()

    @GET @Path("/{productId}/audit") @RolesAllowed("admin")
    fun auditLog(
        @PathParam("productId") productId: UUID,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ) = inventoryPort.getAuditLog(productId, page, size)

    private fun Product.toStockResponse() = StockLevelResponse(id, sku, name, stockQuantity, reorderPoint)
}
