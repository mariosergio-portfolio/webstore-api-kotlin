package com.mycompany.webstore.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "category")
class CategoryJpaEntity {
    @Id var id: UUID = UUID.randomUUID()
    @Column(nullable = false) var name: String = ""
    @Column(unique = true, nullable = false) var slug: String = ""
    @Column(name = "parent_id") var parentId: UUID? = null
    @Column(name = "sort_order", nullable = false) var sortOrder: Int = 0
}
