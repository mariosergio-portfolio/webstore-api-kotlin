package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.application.port.out.CouponRepository
import com.mycompany.webstore.application.port.out.InventoryAuditRepository
import com.mycompany.webstore.application.port.out.ShippingMethodRepository
import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.infrastructure.persistence.repository.*
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class CouponPersistenceAdapterImpl(
    private val repo: CouponPanacheRepository,
    private val mapper: CouponPersistenceMapper,
) : CouponRepository {

    override fun save(coupon: Coupon): Coupon {
        val entity = mapper.toJpa(coupon); repo.getEntityManager().merge(entity); return mapper.toDomain(entity)
    }
    override fun findById(id: UUID): Coupon? = repo.findById(id)?.let(mapper::toDomain)
    override fun findByCode(code: String): Coupon? = repo.find("code", code).firstResult()?.let(mapper::toDomain)
    override fun findAll(active: Boolean?, page: Int, size: Int): List<Coupon> =
        if (active != null) repo.find("active", active).page(page, size).list().map(mapper::toDomain)
        else repo.findAll().page(page, size).list().map(mapper::toDomain)
    override fun existsByCode(code: String): Boolean = repo.count("code", code) > 0
}

@ApplicationScoped
class ShippingMethodPersistenceAdapterImpl(
    private val repo: ShippingMethodPanacheRepository,
    private val mapper: ShippingMethodPersistenceMapper,
) : ShippingMethodRepository {
    override fun findById(id: UUID): ShippingMethod? = repo.findById(id)?.let(mapper::toDomain)
    override fun findAllActive(): List<ShippingMethod> = repo.find("active", true).list().map(mapper::toDomain)
}

@ApplicationScoped
class InventoryAuditPersistenceAdapterImpl(
    private val repo: InventoryAuditLogPanacheRepository,
    private val mapper: InventoryAuditPersistenceMapper,
) : InventoryAuditRepository {
    override fun save(log: InventoryAuditLog): InventoryAuditLog {
        val entity = mapper.toJpa(log); repo.getEntityManager().merge(entity); return mapper.toDomain(entity)
    }
    override fun findByProductId(productId: UUID, page: Int, size: Int): List<InventoryAuditLog> =
        repo.find("productId = ?1 order by createdAt desc", productId).page(page, size).list().map(mapper::toDomain)
}
