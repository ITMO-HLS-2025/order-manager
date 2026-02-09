package ru.itmo.hls.ordermanager.application.port

import ru.itmo.hls.ordermanager.application.dto.SeatPriceDto

interface SeatClient {
    fun getSeat(showId: Long, seatIds: List<Long>): List<SeatPriceDto>
}
