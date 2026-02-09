package ru.itmo.hls.ordermanager.infrastructure.client

import org.springframework.stereotype.Component
import ru.itmo.hls.ordermanager.application.dto.ShowAccessDto
import ru.itmo.hls.ordermanager.application.dto.TheatreAccessDto

@Component
class ShowFeignClientFallback : ShowFeignClient {
    override fun getShow(id: Long): ShowAccessDto = ShowAccessDto(id = id, theatre = TheatreAccessDto(id = null))
}
