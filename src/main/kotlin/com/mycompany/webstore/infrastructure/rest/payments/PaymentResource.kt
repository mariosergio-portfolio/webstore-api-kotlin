package com.mycompany.webstore.infrastructure.rest.payments

import com.mycompany.webstore.application.port.`in`.PaymentPort
import com.mycompany.webstore.domain.model.Payment
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.net.URI
import java.util.UUID

@Tag(name = "Payments")
@Path("/api/payments")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PaymentResource(private val paymentPort: PaymentPort) {

    @POST @RolesAllowed("user", "admin")
    fun initiate(@Valid request: InitiatePaymentRequest): Response {
        val payment = paymentPort.initiate(request.orderId, request.gateway)
        return Response.created(URI.create("/api/payments/${request.orderId}")).entity(payment.toResponse()).build()
    }

    @GET @Path("/{orderId}") @RolesAllowed("user", "admin")
    fun status(@PathParam("orderId") orderId: UUID): PaymentResponse = paymentPort.getByOrderId(orderId).toResponse()
}

@Tag(name = "Payment Webhooks")
@Path("/api/payments/webhook")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class WebhookResource(private val paymentPort: PaymentPort) {

    @POST @Path("/stripe") @PermitAll
    fun stripe(payload: String, @HeaderParam("Stripe-Signature") sig: String): Response {
        paymentPort.handleStripeWebhook(payload, sig)
        return Response.ok().build()
    }

    @POST @Path("/paypal") @PermitAll
    fun paypal(payload: String, @HeaderParam("PAYPAL-TRANSMISSION-SIG") sig: String): Response {
        paymentPort.handlePayPalWebhook(payload, sig)
        return Response.ok().build()
    }

    @POST @Path("/mercadopago") @PermitAll
    fun mercadopago(payload: String, @HeaderParam("x-signature") sig: String): Response {
        paymentPort.handleMercadoPagoWebhook(payload, sig)
        return Response.ok().build()
    }
}

private fun Payment.toResponse() = PaymentResponse(id, orderId, gateway, status.name, amount.amount, amount.currency, createdAt)
