package ru.itmo.hls.ordermanager.client

import org.springframework.stereotype.Component
import ru.itmo.hls.ordermanager.dto.SeatPriceDto

@Component
class SeatFeignClientFallback : SeatFeignClient {
    override fun getSeat(showId: Long, seatIds: List<Long>): List<SeatPriceDto> = emptyList()
}
