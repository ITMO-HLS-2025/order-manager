package ru.itmo.hls.ordermanager.application.dto

data class SeatPriceDto(
    val id: Long,
    val raw: Int,
    val number: Int,
    val price: Int
)
