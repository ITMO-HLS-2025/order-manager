package ru.itmo.hls.ordermanager.mapper

import ru.itmo.hls.ordermanager.dto.OrderDto
import ru.itmo.hls.ordermanager.dto.OrderPayload
import ru.itmo.hls.ordermanager.entity.Order
import ru.itmo.hls.ordermanager.entity.Ticket
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


fun OrderPayload.toEntity(tickets: MutableList<Ticket>, price: Int): Order =
    Order(
        id = 0,
        createdAt = LocalDateTime.now(),
        reservedAt = LocalDateTime.now().plus(90, ChronoUnit.MINUTES),
        tickets = tickets,
        sumPrice = price
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
