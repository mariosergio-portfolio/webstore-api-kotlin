package com.mycompany.webstore.infrastructure.config

import com.mycompany.webstore.shared.exception.*
import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import java.time.Instant

@Provider
class GlobalExceptionMapper : ExceptionMapper<Exception> {
    override fun toResponse(ex: Exception): Response {
        val (status, message) = when (ex) {
            is ResourceNotFoundException        -> 404 to ex.message
            is DuplicateResourceException       -> 409 to ex.message
            is InsufficientStockException       -> 409 to ex.message
            is BusinessRuleException            -> 422 to ex.message
            is InvalidStatusTransitionException -> 422 to ex.message
            is ConstraintViolationException     -> 400 to ex.constraintViolations.joinToString("; ") { "${it.propertyPath}: ${it.message}" }
            else                                -> 500 to "Internal server error"
        }
        val body = mapOf("status" to status, "message" to message, "timestamp" to Instant.now().toString())
        return Response.status(status).entity(body).type(MediaType.APPLICATION_JSON).build()
    }
}
