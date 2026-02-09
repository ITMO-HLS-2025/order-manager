package ru.itmo.hls.ordermanager.domain.port

import ru.itmo.hls.ordermanager.domain.model.Order
import ru.itmo.hls.ordermanager.domain.model.OrderStatus
import java.time.LocalDateTime

interface OrderRepository {
    fun findAllByStatusAndReservedAtBefore(status: OrderStatus, reservedAt: LocalDateTime): List<Order>
    fun findOrderById(id: Long): Order?
    fun findOrderByIdAndUserId(id: Long, userId: Long): Order?
    fun existsByIdAndUserId(id: Long, userId: Long): Boolean
    fun save(order: Order): Order
}
