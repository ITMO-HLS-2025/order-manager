package ru.itmo.hls.ordermanager.application.dto

import java.time.LocalDateTime

data class ApiErrorDto(
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
