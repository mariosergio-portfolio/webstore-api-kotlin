package com.mycompany.webstore.application.port.out

import com.mycompany.webstore.domain.model.Coupon
import java.util.UUID

interface CouponRepository {
    fun save(coupon: Coupon): Coupon
    fun findById(id: UUID): Coupon?
    fun findByCode(code: String): Coupon?
    fun findAll(active: Boolean?, page: Int, size: Int): List<Coupon>
    fun existsByCode(code: String): Boolean
}
