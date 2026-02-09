package ru.itmo.hls.ordermanager.presentation.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest
import ru.itmo.hls.ordermanager.application.dto.OrderDto
import ru.itmo.hls.ordermanager.application.dto.OrderPayload
import ru.itmo.hls.ordermanager.application.dto.TicketDto
import ru.itmo.hls.ordermanager.application.usecase.OrderService
import ru.itmo.hls.ordermanager.infrastructure.security.JwtClaimsService

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "Order and tickets management APIs")
open class OrderController(
    private val orderService: OrderService,
    private val paginationValidator: PaginationValidator,
    private val jwtClaimsService: JwtClaimsService
) {

    @Operation(summary = "Резервирование билетов")
    @PostMapping("/reserve")
    fun reserveTickets(
        @RequestBody payload: OrderPayload,
        request: HttpServletRequest
    ): OrderDto {
        val claims = jwtClaimsService.requireClaims(request.getHeader(HttpHeaders.AUTHORIZATION))
        return orderService.reserveTickets(payload, claims.userId)
    }

    @Operation(summary = "Оплата билетов по заказу")
    @PostMapping("/{orderId}/pay")
    fun payTickets(
        @PathVariable orderId: Long,
        request: HttpServletRequest
    ): OrderDto {
        val claims = jwtClaimsService.requireClaims(request.getHeader(HttpHeaders.AUTHORIZATION))
        return orderService.payTickets(orderId, claims.userId)
    }

    @Operation(summary = "Получение билетов по заказу с постраничной прокруткой")
    @GetMapping("/{orderId}/tickets")
    fun getTicketsPage(
        @PathVariable orderId: Long,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 10,
        request: HttpServletRequest
    ): ResponseEntity<List<TicketDto>> {
        paginationValidator.validateSize(size)
        val claims = jwtClaimsService.requireClaims(request.getHeader(HttpHeaders.AUTHORIZATION))
        val resultPage = orderService.getTicketsPageByOrderId(
            orderId,
            page,
            size,
            claims.userId,
            claims.role,
            claims.theatreId
        )
        return ResponseEntity.ok()
            .header("X-Has-Next-Page", resultPage.hasNext().toString())
            .body(resultPage.content)
    }

    @Operation(summary = "Получение заказа (reactive + JPA)")
    @GetMapping("/{orderId}")
    fun getOrderReactive(
        @PathVariable orderId: Long,
        request: HttpServletRequest
    ): Mono<OrderDto> {
        val claims = jwtClaimsService.requireClaims(request.getHeader(HttpHeaders.AUTHORIZATION))
        return Mono.fromCallable {
            orderService.getOrderById(orderId, claims.userId, claims.role, claims.theatreId)
        }
            .subscribeOn(Schedulers.boundedElastic())
    }
}
