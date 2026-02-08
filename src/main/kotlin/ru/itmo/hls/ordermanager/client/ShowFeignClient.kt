package ru.itmo.hls.ordermanager.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.itmo.hls.ordermanager.dto.ShowAccessDto

@FeignClient(
    name = "show-manager",
    path = "/api/shows",
    fallback = ShowFeignClientFallback::class
)
interface ShowFeignClient {
    @GetMapping("/{id}")
    fun getShow(@PathVariable("id") id: Long): ShowAccessDto
}
