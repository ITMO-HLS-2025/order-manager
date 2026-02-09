package ru.itmo.hls.ordermanager.application.dto

import ru.itmo.hls.ordermanager.domain.model.OrderStatus
import java.time.LocalDateTime


data class OrderDto(
    val id: Long,
    val createdAt: LocalDateTime,
    val reservedAt: LocalDateTime?,
    val status: OrderStatus = OrderStatus.RESERVED,
    val seatIds: List<Long>,
    val price: Int
)

data class OrderPayload(
    val seatIds: List<Long>,
    val showId: Long
)
