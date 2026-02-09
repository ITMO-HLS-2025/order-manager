package ru.itmo.hls.ordermanager.infrastructure.db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.hls.ordermanager.domain.model.Order
import ru.itmo.hls.ordermanager.domain.model.OrderStatus
import java.time.LocalDateTime

@Repository
interface OrderJpaRepository : JpaRepository<Order, Long> {
    @Query("SELECT e FROM Order e join fetch e.tickets WHERE e.status = :status AND e.reservedAt <= :reservedAt")
    fun findAllByStatusAndReservedAtBefore(status: OrderStatus, reservedAt: LocalDateTime) : List<Order>
    fun findOrderById(id: Long): Order?
    fun findOrderByIdAndUserId(id: Long, userId: Long): Order?
    fun existsByIdAndUserId(id: Long, userId: Long): Boolean
}
