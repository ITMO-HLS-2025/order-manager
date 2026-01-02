package ru.itmo.hls.ordermanager.dto

data class SeatPriceDto(
    val id: Long,
    val raw: Int,
    val number: Int,
    val price: Int
)