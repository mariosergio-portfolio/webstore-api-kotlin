package com.mycompany.webstore.infrastructure.rest.catalog

import com.mycompany.webstore.domain.model.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.time.Instant
import java.util.UUID

@Mapper(componentModel = "cdi")
interface ProductRestMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "price", expression = "java(new com.mycompany.webstore.domain.model.Money(request.getPrice().getAmount(), request.getPrice().getCurrency()))")
    @Mapping(target = "version", constant = "0L")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "reorderPoint", constant = "5")
    fun toDomain(request: ProductRequest): Product

    @Mapping(target = "price", expression = "java(new com.mycompany.webstore.infrastructure.rest.catalog.MoneyDto(domain.getPrice().getAmount(), domain.getPrice().getCurrency()))")
    fun toResponse(domain: Product): ProductResponse
}

@Mapper(componentModel = "cdi")
interface CategoryRestMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    fun toDomain(request: CategoryRequest): Category
    fun toResponse(domain: Category): CategoryResponse
}
