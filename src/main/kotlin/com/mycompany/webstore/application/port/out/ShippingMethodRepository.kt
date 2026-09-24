package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.ShippingMethod
import java.util.UUID

interface ShippingMethodRepository {
    fun findById(id: UUID): ShippingMethod?
    fun findAllActive(): List<ShippingMethod>
}
