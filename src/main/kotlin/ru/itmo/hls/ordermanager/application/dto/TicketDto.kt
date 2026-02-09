package ru.itmo.hls.ordermanager.application.dto

data class TicketDto(
    val id: Long,
    val price: Int,
    val raw: Int,
    val number: Int
)
