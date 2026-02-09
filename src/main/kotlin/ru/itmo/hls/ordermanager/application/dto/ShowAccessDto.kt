package ru.itmo.hls.ordermanager.application.dto

data class ShowAccessDto(
    val id: Long? = null,
    val theatre: TheatreAccessDto? = null
)

data class TheatreAccessDto(
    val id: Long? = null
)
