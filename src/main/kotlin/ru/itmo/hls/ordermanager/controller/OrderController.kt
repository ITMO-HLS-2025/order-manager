package ru.itmo.hls.ordermanager.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.itmo.hls.ordermanager.dto.OrderDto
import ru.itmo.hls.ordermanager.dto.OrderPayload
import ru.itmo.hls.ordermanager.dto.TicketDto
import ru.itmo.hls.ordermanager.service.OrderService

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "Order and tickets management APIs")
open class OrderController(
    private val orderService: OrderService,
    private val paginationValidator: PaginationValidator
) {

    @Operation(summary = "Резервирование билетов")
    @PostMapping("/reserve")
    fun reserveTickets(@RequestBody payload: OrderPayload): OrderDto {
        return orderService.reserveTickets(payload)
    }

    @Operation(summary = "Оплата билетов по заказу")
    @PostMapping("/{orderId}/pay")
    fun payTickets(@PathVariable orderId: Long): OrderDto {
        return orderService.payTickets(orderId)
    }

    @Operation(summary = "Получение билетов по заказу с постраничной прокруткой")
    @GetMapping("/{orderId}/tickets")
    fun getTicketsPage(
        @PathVariable orderId: Long,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 10
    ): ResponseEntity<List<TicketDto>> {
        paginationValidator.validateSize(size)
        val resultPage = orderService.getTicketsPageByOrderId(orderId, page, size)
        return ResponseEntity.ok()
            .header("X-Has-Next-Page", resultPage.hasNext().toString())
            .body(resultPage.content)
    }

    @Operation(summary = "Получение заказа (reactive + JPA)")
    @GetMapping("/{orderId}")
    fun getOrderReactive(@PathVariable orderId: Long): Mono<OrderDto> {
        return Mono.fromCallable { orderService.getOrderById(orderId) }
            .subscribeOn(Schedulers.boundedElastic())
    }
}
