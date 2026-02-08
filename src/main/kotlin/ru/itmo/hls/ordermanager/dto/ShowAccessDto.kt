package ru.itmo.hls.ordermanager.dto

data class ShowAccessDto(
    val id: Long? = null,
    val theatre: TheatreAccessDto? = null
)

data class TheatreAccessDto(
    val id: Long? = null
)
