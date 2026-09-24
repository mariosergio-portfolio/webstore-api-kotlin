package com.mycompany.webstore.application.service

import com.mycompany.webstore.application.port.`in`.CouponPort
import com.mycompany.webstore.application.port.out.CouponRepository
import com.mycompany.webstore.domain.model.Coupon
import com.mycompany.webstore.shared.exception.DuplicateResourceException
import com.mycompany.webstore.shared.exception.ResourceNotFoundException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.UUID

@ApplicationScoped
class CouponPortImpl(
    private val couponRepository: CouponRepository,
) : CouponPort {

    @Transactional
    override fun create(coupon: Coupon): Coupon {
        if (couponRepository.existsByCode(coupon.code))
            throw DuplicateResourceException("Coupon code '${coupon.code}' already exists")
        return couponRepository.save(coupon)
    }

    @Transactional
    override fun update(id: UUID, coupon: Coupon): Coupon {
        findById(id)
        return couponRepository.save(coupon.copy(id = id))
    }

    override fun findById(id: UUID): Coupon =
        couponRepository.findById(id) ?: throw ResourceNotFoundException("Coupon $id not found")

    override fun findByCode(code: String): Coupon =
        couponRepository.findByCode(code) ?: throw ResourceNotFoundException("Coupon '$code' not found")

    override fun findAll(active: Boolean?, page: Int, size: Int): List<Coupon> =
        couponRepository.findAll(active, page, size)

    @Transactional
    override fun activate(id: UUID): Coupon = couponRepository.save(findById(id).copy(active = true))

    @Transactional
    override fun deactivate(id: UUID): Coupon = couponRepository.save(findById(id).copy(active = false))
}
