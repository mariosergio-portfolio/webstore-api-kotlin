package com.mycompany.webstore.domain.model

import java.util.UUID

data class Category(
    val id: UUID,
    val name: String,
    val slug: String,
    val parentId: UUID?,
    val sortOrder: Int = 0,
)
