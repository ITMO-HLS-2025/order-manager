package ru.itmo.hls.ordermanager.application.mapper

import ru.itmo.hls.ordermanager.application.dto.OrderDto
import ru.itmo.hls.ordermanager.application.dto.OrderPayload
import ru.itmo.hls.ordermanager.domain.model.Order
import ru.itmo.hls.ordermanager.domain.model.Ticket
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


fun OrderPayload.toEntity(tickets: MutableList<Ticket>, price: Int, userId: Long): Order =
    Order(
        id = 0,
        createdAt = LocalDateTime.now(),
        reservedAt = LocalDateTime.now().plus(90, ChronoUnit.MINUTES),
        tickets = tickets,
        sumPrice = price,
        userId = userId
    )

fun Order.toDto(): OrderDto =
    OrderDto(
        id = id,
        reservedAt = reservedAt,
        status = status,
        seatIds = tickets.mapNotNull { it.seatId },
        price = sumPrice,
        createdAt = createdAt
    )
