package com.mycompany.webstore.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.quarkus.jackson.ObjectMapperCustomizer
import jakarta.inject.Singleton

/**
 * Registers the Jackson Kotlin module so that Kotlin data classes (which have
 * no default no-arg constructor) can be deserialized from JSON.
 *
 * Without this, Jackson throws:
 *   InvalidDefinitionException: Cannot construct instance of `...` (no Creators,
 *   like default constructor, exist)
 *
 * Quarkus does NOT auto-register KotlinModule even when quarkus-kotlin or
 * quarkus-jackson is on the classpath — it must be wired explicitly via
 * ObjectMapperCustomizer.
 */
@Singleton
class JacksonConfig : ObjectMapperCustomizer {
    override fun customize(mapper: ObjectMapper) {
        mapper.registerModule(KotlinModule.Builder().build())
    }
}
