package ru.itmo.hls.ordermanager.infrastructure.db

import org.springframework.stereotype.Repository
import ru.itmo.hls.ordermanager.domain.model.Order
import ru.itmo.hls.ordermanager.domain.model.OrderStatus
import ru.itmo.hls.ordermanager.domain.port.OrderRepository
import java.time.LocalDateTime

@Repository
class OrderRepositoryAdapter(
    private val orderJpaRepository: OrderJpaRepository
) : OrderRepository {

    override fun findAllByStatusAndReservedAtBefore(
        status: OrderStatus,
        reservedAt: LocalDateTime
    ): List<Order> = orderJpaRepository.findAllByStatusAndReservedAtBefore(status, reservedAt)

    override fun findOrderById(id: Long): Order? = orderJpaRepository.findOrderById(id)

    override fun findOrderByIdAndUserId(id: Long, userId: Long): Order? =
        orderJpaRepository.findOrderByIdAndUserId(id, userId)

    override fun existsByIdAndUserId(id: Long, userId: Long): Boolean =
        orderJpaRepository.existsByIdAndUserId(id, userId)

    override fun save(order: Order): Order = orderJpaRepository.save(order)
}
