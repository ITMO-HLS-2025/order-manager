package ru.itmo.hls.ordermanager.application.port

import ru.itmo.hls.ordermanager.application.dto.ShowAccessDto

interface ShowClient {
    fun getShow(showId: Long): ShowAccessDto
}
