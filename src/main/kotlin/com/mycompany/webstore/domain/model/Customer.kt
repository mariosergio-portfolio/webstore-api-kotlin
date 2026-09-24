package com.mycompany.webstore.domain.model

import java.time.Instant
import java.util.UUID

data class Customer(
    val id: UUID,
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val active: Boolean,
    val addresses: List<CustomerAddress>,
    val createdAt: Instant,
)

data class CustomerAddress(
    val id: UUID,
    val customerId: UUID,
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean,
)
