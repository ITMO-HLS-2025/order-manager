package ru.itmo.hls.ordermanager.infrastructure.client

import org.springframework.stereotype.Component
import ru.itmo.hls.ordermanager.application.dto.SeatPriceDto

@Component
class SeatFeignClientFallback : SeatFeignClient {
    override fun getSeat(showId: Long, seatIds: List<Long>): List<SeatPriceDto> = emptyList()
}
