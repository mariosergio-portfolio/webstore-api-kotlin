package com.mycompany.webstore.shared.exception

class ResourceNotFoundException(message: String) : RuntimeException(message)
class BusinessRuleException(message: String) : RuntimeException(message)
class InsufficientStockException(val items: List<StockConflict>, message: String) : RuntimeException(message)
class InvalidStatusTransitionException(message: String) : RuntimeException(message)
class DuplicateResourceException(message: String) : RuntimeException(message)

data class StockConflict(val productId: java.util.UUID, val requested: Int, val available: Int)
