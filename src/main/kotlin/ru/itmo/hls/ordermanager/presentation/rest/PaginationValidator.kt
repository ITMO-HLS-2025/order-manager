package ru.itmo.hls.ordermanager.presentation.rest

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PaginationValidator(
    @Value("\${spring.application.pagination.max-size:50}")
    private val maxSize: Int
) {

    fun validateSize(size: Int) {
        require(size > 0) { "Page size must be positive" }
        require(size <= maxSize) { "Page size must be <= $maxSize" }
    }
}
