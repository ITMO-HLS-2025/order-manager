package ru.itmo.hls.ordermanager.infrastructure.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.itmo.hls.ordermanager.application.dto.ShowAccessDto
import ru.itmo.hls.ordermanager.application.port.ShowClient

@FeignClient(
    name = "show-manager",
    path = "/api/shows",
    fallback = ShowFeignClientFallback::class
)
interface ShowFeignClient : ShowClient {
    @GetMapping("/{id}")
    override fun getShow(@PathVariable("id") id: Long): ShowAccessDto
}
