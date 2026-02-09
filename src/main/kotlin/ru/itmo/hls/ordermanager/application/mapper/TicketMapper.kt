package ru.itmo.hls.ordermanager.application.mapper

import ru.itmo.hls.ordermanager.application.dto.SeatPriceDto
import ru.itmo.hls.ordermanager.domain.model.Ticket
import ru.itmo.hls.ordermanager.domain.model.TicketStatus

fun SeatPriceDto.toEntity(showId: Long): Ticket = Ticket(
    showId = showId,
    seatId = id,
    status = TicketStatus.RESERVED
)
