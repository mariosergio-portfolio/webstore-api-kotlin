package com.mycompany.webstore.infrastructure.rest.admin

import com.mycompany.webstore.application.port.`in`.CouponPort
import com.mycompany.webstore.domain.model.Coupon
import com.mycompany.webstore.domain.model.DiscountType
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.math.BigDecimal
import java.net.URI
import java.time.Instant
import java.util.UUID

data class CouponRequest(
    @field:NotBlank val code: String,
    @field:NotNull val discountType: DiscountType,
    @field:DecimalMin("0.01") val value: BigDecimal,
    val minOrderAmount: BigDecimal?,
    val maxUses: Int?,
    val expiresAt: Instant?,
)
data class CouponResponse(val id: UUID, val code: String, val discountType: DiscountType, val value: BigDecimal, val active: Boolean, val usedCount: Int, val expiresAt: Instant?)

@Tag(name = "Admin - Coupons")
@Path("/api/admin/coupons")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CouponResource(private val couponPort: CouponPort) {

    @GET @RolesAllowed("admin")
    fun list(@QueryParam("active") active: Boolean?, @QueryParam("page") @DefaultValue("0") page: Int, @QueryParam("size") @DefaultValue("20") size: Int): List<CouponResponse> =
        couponPort.findAll(active, page, size).map { it.toResponse() }

    @POST @RolesAllowed("admin")
    fun create(@Valid request: CouponRequest): Response {
        val created = couponPort.create(request.toDomain())
        return Response.created(URI.create("/api/admin/coupons/${created.id}")).entity(created.toResponse()).build()
    }

    @PUT @Path("/{id}") @RolesAllowed("admin")
    fun update(@PathParam("id") id: UUID, @Valid request: CouponRequest): CouponResponse =
        couponPort.update(id, request.toDomain()).toResponse()

    @PATCH @Path("/{id}/activate") @RolesAllowed("admin")
    fun activate(@PathParam("id") id: UUID): CouponResponse = couponPort.activate(id).toResponse()

    @PATCH @Path("/{id}/deactivate") @RolesAllowed("admin")
    fun deactivate(@PathParam("id") id: UUID): CouponResponse = couponPort.deactivate(id).toResponse()

    private fun CouponRequest.toDomain() = Coupon(UUID.randomUUID(), code, discountType, value, minOrderAmount, maxUses, 0, expiresAt, true)
    private fun Coupon.toResponse() = CouponResponse(id, code, discountType, value, active, usedCount, expiresAt)
}
