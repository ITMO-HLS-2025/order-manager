package ru.itmo.hls.ordermanager.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import ru.itmo.hls.ordermanager.dto.OrderDto
import ru.itmo.hls.ordermanager.dto.OrderPayload
import ru.itmo.hls.ordermanager.dto.TicketDto
import ru.itmo.hls.ordermanager.entity.OrderStatus
import ru.itmo.hls.ordermanager.exception.NotFreeSeatException
import ru.itmo.hls.ordermanager.exception.OrderNotFoundException
import ru.itmo.hls.ordermanager.service.OrderService
import java.time.LocalDateTime

@WebMvcTest(OrderController::class)
@Import(PaginationValidator::class, OrderManagerExceptionHandler::class)
@TestPropertySource(properties = ["spring.application.pagination.max-size=2"])
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var orderService: OrderService

    @Test
    fun reserveTickets() {
        val payload = OrderPayload(showId = 1L, seatIds = listOf(3L))
        val orderDto = OrderDto(
            id = 10L,
            createdAt = LocalDateTime.now(),
            reservedAt = LocalDateTime.now().plusMinutes(10),
            status = OrderStatus.RESERVED,
            seatIds = listOf(3L),
            price = 900
        )
        `when`(orderService.reserveTickets(payload)).thenReturn(orderDto)

        mockMvc.post("/api/orders/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(payload)
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(10) }
            jsonPath("$.price") { value(900) }
        }
    }

    @Test
    fun reserveTicketsConflict() {
        val payload = OrderPayload(showId = 1L, seatIds = listOf(1L))
        `when`(orderService.reserveTickets(payload)).thenThrow(
            NotFreeSeatException("Невозможно создать заказ -- есть занятые места")
        )

        mockMvc.post("/api/orders/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(payload)
        }.andExpect {
            status { isConflict() }
            jsonPath("$.message") { value("Невозможно создать заказ -- есть занятые места") }
        }
    }

    @Test
    fun payTickets() {
        val orderDto = OrderDto(
            id = 10L,
            createdAt = LocalDateTime.now(),
            reservedAt = LocalDateTime.now().plusMinutes(10),
            status = OrderStatus.PAID,
            seatIds = listOf(3L),
            price = 900
        )
        `when`(orderService.payTickets(10L)).thenReturn(orderDto)

        mockMvc.post("/api/orders/10/pay") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("PAID") }
        }
    }

    @Test
    fun payTicketsNotFound() {
        `when`(orderService.payTickets(10L)).thenThrow(OrderNotFoundException("Заказ не найден: id=10"))

        mockMvc.post("/api/orders/10/pay") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message") { value("Заказ не найден: id=10") }
        }
    }

    @Test
    fun payTicketsIllegalState() {
        `when`(orderService.payTickets(10L)).thenThrow(IllegalStateException("Заказ уже оплачен"))

        mockMvc.post("/api/orders/10/pay") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isConflict() }
            jsonPath("$.message") { value("Заказ уже оплачен") }
        }
    }

    @Test
    fun getTicketsPage() {
        val tickets = listOf(
            TicketDto(id = 1L, price = 500, raw = 1, number = 1),
            TicketDto(id = 2L, price = 900, raw = 1, number = 2)
        )
        val page = PageImpl(tickets, PageRequest.of(0, 2), 3)
        `when`(orderService.getTicketsPageByOrderId(1L, 0, 2)).thenReturn(page)

        mockMvc.get("/api/orders/1/tickets") {
            param("page", "0")
            param("size", "2")
        }.andExpect {
            status { isOk() }
            header { string("X-Has-Next-Page", "true") }
            jsonPath("$[0].id") { value(1) }
            jsonPath("$[1].id") { value(2) }
        }
    }

    @Test
    fun getTicketsPageRejectsLargeSize() {
        mockMvc.get("/api/orders/1/tickets") {
            param("page", "0")
            param("size", "3")
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("Page size must be <= 2") }
        }
    }
}
