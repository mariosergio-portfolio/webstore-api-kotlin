package com.mycompany.webstore.infrastructure.persistence.adapter

import com.mycompany.webstore.domain.model.*
import com.mycompany.webstore.infrastructure.persistence.entity.*
import org.mapstruct.*

@Mapper(componentModel = "cdi")
interface ProductPersistenceMapper {
    @Mapping(source = "price.amount", target = "price")
    @Mapping(source = "price.currency", target = "currency")
    fun toJpa(domain: Product): ProductJpaEntity

    @Mapping(target = "price", expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getPrice(), entity.getCurrency()))")
    @Mapping(target = "imageUrls", expression = "java(entity.getImages().stream().sorted(java.util.Comparator.comparingInt(com.mycompany.webstore.infrastructure.persistence.entity.ProductImageJpaEntity::getSortOrder)).map(com.mycompany.webstore.infrastructure.persistence.entity.ProductImageJpaEntity::getUrl).collect(java.util.stream.Collectors.toList()))")
    fun toDomain(entity: ProductJpaEntity): Product

    fun statusToString(status: ProductStatus): String = status.name
    fun statusFromString(status: String): ProductStatus = ProductStatus.valueOf(status)
}

@Mapper(componentModel = "cdi")
interface CategoryPersistenceMapper {
    fun toJpa(domain: Category): CategoryJpaEntity
    fun toDomain(entity: CategoryJpaEntity): Category
}

@Mapper(componentModel = "cdi")
interface CartPersistenceMapper {
    fun toJpa(domain: Cart): CartJpaEntity
    fun toDomain(entity: CartJpaEntity): Cart
    @Mapping(source = "unitPrice.amount", target = "unitPrice")
    @Mapping(source = "unitPrice.currency", target = "unitPriceCurrency")
    fun itemToJpa(domain: CartItem): CartItemJpaEntity
    @Mapping(target = "unitPrice", expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getUnitPrice(), entity.getUnitPriceCurrency()))")
    fun itemToDomain(entity: CartItemJpaEntity): CartItem
}

@Mapper(componentModel = "cdi")
interface OrderPersistenceMapper {
    @Mapping(target = "shippingStreet",      source = "shippingAddress.street")
    @Mapping(target = "shippingCity",        source = "shippingAddress.city")
    @Mapping(target = "shippingState",       source = "shippingAddress.state")
    @Mapping(target = "shippingPostalCode",  source = "shippingAddress.postalCode")
    @Mapping(target = "shippingCountry",     source = "shippingAddress.country")
    @Mapping(target = "billingStreet",       source = "billingAddress.street")
    @Mapping(target = "billingCity",         source = "billingAddress.city")
    @Mapping(target = "billingState",        source = "billingAddress.state")
    @Mapping(target = "billingPostalCode",   source = "billingAddress.postalCode")
    @Mapping(target = "billingCountry",      source = "billingAddress.country")
    @Mapping(target = "subtotal",            source = "subtotal.amount")
    @Mapping(target = "discountAmount",      source = "discountAmount.amount")
    @Mapping(target = "shippingCost",        source = "shippingCost.amount")
    @Mapping(target = "total",               source = "total.amount")
    @Mapping(target = "currency",            source = "subtotal.currency")
    fun toJpa(domain: Order): OrderJpaEntity

    @Mapping(target = "shippingAddress", expression = "java(new com.mycompany.webstore.domain.model.Address(entity.getShippingStreet(),entity.getShippingCity(),entity.getShippingState(),entity.getShippingPostalCode(),entity.getShippingCountry()))")
    @Mapping(target = "billingAddress",  expression = "java(entity.getBillingStreet() != null ? new com.mycompany.webstore.domain.model.Address(entity.getBillingStreet(),entity.getBillingCity(),entity.getBillingState(),entity.getBillingPostalCode(),entity.getBillingCountry()) : null)")
    @Mapping(target = "subtotal",       expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getSubtotal(), entity.getCurrency()))")
    @Mapping(target = "discountAmount", expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getDiscountAmount(), entity.getCurrency()))")
    @Mapping(target = "shippingCost",   expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getShippingCost(), entity.getCurrency()))")
    @Mapping(target = "total",          expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getTotal(), entity.getCurrency()))")
    fun toDomain(entity: OrderJpaEntity): Order

    @Mapping(target = "unitPrice", source = "unitPrice.amount")
    @Mapping(target = "unitPriceCurrency", source = "unitPrice.currency")
    @Mapping(target = "lineTotal", source = "lineTotal.amount")
    fun itemToJpa(domain: OrderItem): OrderItemJpaEntity

    @Mapping(target = "unitPrice", expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getUnitPrice(), entity.getUnitPriceCurrency()))")
    @Mapping(target = "lineTotal", expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getLineTotal(), entity.getUnitPriceCurrency()))")
    fun itemToDomain(entity: OrderItemJpaEntity): OrderItem
}

@Mapper(componentModel = "cdi")
interface PaymentPersistenceMapper {
    @Mapping(target = "amount",   source = "amount.amount")
    @Mapping(target = "currency", source = "amount.currency")
    fun toJpa(domain: Payment): PaymentJpaEntity

    @Mapping(target = "amount", expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getAmount(), entity.getCurrency()))")
    fun toDomain(entity: PaymentJpaEntity): Payment
}

@Mapper(componentModel = "cdi")
interface CustomerPersistenceMapper {
    fun toJpa(domain: Customer): CustomerJpaEntity
    fun toDomain(entity: CustomerJpaEntity): Customer
    fun addressToJpa(domain: CustomerAddress): CustomerAddressJpaEntity
    fun addressToDomain(entity: CustomerAddressJpaEntity): CustomerAddress
}

@Mapper(componentModel = "cdi")
interface CouponPersistenceMapper {
    fun toJpa(domain: Coupon): CouponJpaEntity
    fun toDomain(entity: CouponJpaEntity): Coupon
    fun typeToString(t: DiscountType): String = t.name
    fun typeFromString(t: String): DiscountType = DiscountType.valueOf(t)
}

@Mapper(componentModel = "cdi")
interface ShippingMethodPersistenceMapper {
    @Mapping(target = "cost", expression = "java(new com.mycompany.webstore.domain.model.Money(entity.getCost(), entity.getCurrency()))")
    fun toDomain(entity: com.mycompany.webstore.infrastructure.persistence.entity.ShippingMethodJpaEntity): ShippingMethod
}

@Mapper(componentModel = "cdi")
interface InventoryAuditPersistenceMapper {
    fun toJpa(domain: InventoryAuditLog): InventoryAuditLogJpaEntity
    fun toDomain(entity: InventoryAuditLogJpaEntity): InventoryAuditLog
    fun typeToString(t: InventoryChangeType): String = t.name
    fun typeFromString(t: String): InventoryChangeType = InventoryChangeType.valueOf(t)
}
