package com.mycompany.webstore.infrastructure.config

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Info
import jakarta.enterprise.context.ApplicationScoped

@OpenAPIDefinition(
    info = Info(
        title = "Web Store API",
        description = "REST API for the Web Store e-commerce system — Kotlin + Quarkus",
        version = "1.0.0",
    )
)
@ApplicationScoped
class OpenApiConfig
