package ru.itmo.hls.ordermanager.mapper

import ru.itmo.hls.ordermanager.dto.SeatPriceDto
import ru.itmo.hls.ordermanager.entity.Ticket
import ru.itmo.hls.ordermanager.entity.TicketStatus

fun SeatPriceDto.toEntity(showId: Long): Ticket = Ticket(
    showId = showId,
    seatId = id,
    status = TicketStatus.RESERVED
)
