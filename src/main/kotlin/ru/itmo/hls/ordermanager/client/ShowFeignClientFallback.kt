package ru.itmo.hls.ordermanager.client

import org.springframework.stereotype.Component
import ru.itmo.hls.ordermanager.dto.ShowAccessDto
import ru.itmo.hls.ordermanager.dto.TheatreAccessDto

@Component
class ShowFeignClientFallback : ShowFeignClient {
    override fun getShow(id: Long): ShowAccessDto = ShowAccessDto(id = id, theatre = TheatreAccessDto(id = null))
}
