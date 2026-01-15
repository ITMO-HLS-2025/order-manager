package ru.itmo.hls.ordermanager.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.itmo.hls.ordermanager.dto.SeatPriceDto

@FeignClient(
    name = "theatre-manager",
    path = "/inner",
    fallback = SeatFeignClientFallback::class
)
interface SeatFeignClient {
    @GetMapping("/seats")
    fun getSeat(
        @RequestParam("showId") showId: Long,
        @RequestParam("seats") seats: List<Long>
    ): List<SeatPriceDto>
}
