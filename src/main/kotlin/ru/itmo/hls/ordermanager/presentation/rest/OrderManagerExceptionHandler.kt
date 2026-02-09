package ru.itmo.hls.ordermanager.presentation.rest

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.itmo.hls.ordermanager.application.dto.ApiErrorDto
import ru.itmo.hls.ordermanager.domain.exception.NotFreeSeatException
import ru.itmo.hls.ordermanager.domain.exception.OrderNotFoundException

@RestControllerAdvice
class OrderManagerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException::class)
    fun handleOrderNotFound(ex: OrderNotFoundException): ApiErrorDto {
        return ApiErrorDto(message = ex.message ?: "Order not found")
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(NotFreeSeatException::class)
    fun handleNotFreeSeat(ex: NotFreeSeatException): ApiErrorDto {
        return ApiErrorDto(message = ex.message ?: "Seats are not available")
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ApiErrorDto {
        return ApiErrorDto(message = ex.message ?: "Illegal state")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ApiErrorDto {
        return ApiErrorDto(message = ex.message ?: "Bad request")
    }
}
