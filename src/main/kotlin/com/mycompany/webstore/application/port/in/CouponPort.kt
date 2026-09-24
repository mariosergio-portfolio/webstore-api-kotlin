package com.mycompany.webstore.application.port.`in`

import com.mycompany.webstore.domain.model.Coupon
import java.util.UUID

interface CouponPort {
    fun create(coupon: Coupon): Coupon
    fun update(id: UUID, coupon: Coupon): Coupon
    fun findById(id: UUID): Coupon
    fun findByCode(code: String): Coupon
    fun findAll(active: Boolean?, page: Int, size: Int): List<Coupon>
    fun activate(id: UUID): Coupon
    fun deactivate(id: UUID): Coupon
}
