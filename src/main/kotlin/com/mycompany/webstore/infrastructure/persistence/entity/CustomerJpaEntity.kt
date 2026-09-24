package com.mycompany.webstore.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "customer")
class CustomerJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(unique = true, nullable = false) var email: String = ""
    @Column(name = "full_name", nullable = false) var fullName: String = ""
    @Column(name = "password_hash", nullable = false) var passwordHash: String = ""
    @Column(nullable = false) var active: Boolean = true
    @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now()

    @OneToMany(mappedBy = "customerId", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var addresses: MutableList<CustomerAddressJpaEntity> = mutableListOf()
}

@Entity
@Table(name = "customer_address")
class CustomerAddressJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(name = "customer_id", nullable = false) var customerId: UUID = UUID.randomUUID()
    @Column(nullable = false) var street: String = ""
    @Column(nullable = false) var city: String = ""
    @Column(nullable = false) var state: String = ""
    @Column(name = "postal_code", nullable = false) var postalCode: String = ""
    @Column(nullable = false) var country: String = ""
    @Column(name = "is_default", nullable = false) var isDefault: Boolean = false
}
