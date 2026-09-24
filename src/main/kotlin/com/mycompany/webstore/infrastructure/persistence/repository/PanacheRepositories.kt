package com.mycompany.webstore.infrastructure.persistence.repository

import com.mycompany.webstore.infrastructure.persistence.entity.*
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped class ProductPanacheRepository : PanacheRepositoryBase<ProductJpaEntity, UUID>
@ApplicationScoped class CategoryPanacheRepository : PanacheRepositoryBase<CategoryJpaEntity, UUID>
@ApplicationScoped class CartPanacheRepository : PanacheRepositoryBase<CartJpaEntity, UUID>
@ApplicationScoped class OrderPanacheRepository : PanacheRepositoryBase<OrderJpaEntity, UUID>
@ApplicationScoped class PaymentPanacheRepository : PanacheRepositoryBase<PaymentJpaEntity, UUID>
@ApplicationScoped class CustomerPanacheRepository : PanacheRepositoryBase<CustomerJpaEntity, UUID>
@ApplicationScoped class CouponPanacheRepository : PanacheRepositoryBase<CouponJpaEntity, UUID>
@ApplicationScoped class ShippingMethodPanacheRepository : PanacheRepositoryBase<ShippingMethodJpaEntity, UUID>
@ApplicationScoped class InventoryAuditLogPanacheRepository : PanacheRepositoryBase<InventoryAuditLogJpaEntity, UUID>
