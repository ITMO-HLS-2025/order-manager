package ru.itmo.hls.ordermanager.infrastructure.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.itmo.hls.ordermanager.application.dto.SeatPriceDto
import ru.itmo.hls.ordermanager.application.port.SeatClient

@FeignClient(
    name = "theatre-manager",
    path = "/inner",
    fallback = SeatFeignClientFallback::class
)
interface SeatFeignClient : SeatClient {
    @GetMapping("/seats")
    override fun getSeat(
        @RequestParam("showId") showId: Long,
        @RequestParam("seats") seats: List<Long>
    ): List<SeatPriceDto>
}
