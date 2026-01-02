package ru.itmo.hls.ordermanager.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.mockito.Mockito.verifyNoInteractions
import ru.itmo.hls.ordermanager.AbstractIntegrationTest
import ru.itmo.hls.ordermanager.client.SeatFeignClient
import ru.itmo.hls.ordermanager.dto.OrderPayload
import ru.itmo.hls.ordermanager.entity.OrderStatus
import ru.itmo.hls.ordermanager.entity.TicketStatus
import ru.itmo.hls.ordermanager.exception.NotFreeSeatException
import ru.itmo.hls.ordermanager.exception.OrderNotFoundException
import ru.itmo.hls.ordermanager.repository.OrderRepository
import ru.itmo.hls.ordermanager.repository.TicketRepository
import java.time.LocalDateTime

@SpringBootTest
open class OrderServiceIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var seatFeignClient: SeatFeignClient

    @Test
    @DisplayName("Резервирование билетов — успешный кейс")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun reserveTicketsSuccess() {
        val payload = OrderPayload(showId = 1L, seatIds = listOf(3L))
        val dto = orderService.reserveTickets(payload)

        assertNotNull(dto.id)
        assertEquals(OrderStatus.RESERVED, dto.status)
        assertEquals(900, dto.price)
    }

    @Test
    @DisplayName("Резервирование — ошибка, если место занято")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun reserveTicketsFailIfOccupied() {
        val payload = OrderPayload(showId = 1L, seatIds = listOf(1L)) // место 1 занято
        assertThrows<NotFreeSeatException> {
            orderService.reserveTickets(payload)
        }
    }

    @Test
    @DisplayName("Автоотмена заказов — RESERVED + reserved_at < now")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun cancelExpiredOrders() {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().minusMinutes(10)
        orderRepository.save(order)

        orderService.cancelExpiredOrders()

        val updated = orderRepository.findById(1L).get()
        assertEquals(OrderStatus.CANCELLED, updated.status)
    }

    @Test
    @DisplayName("Автоотмена заказов — нет просроченных")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun cancelExpiredOrdersNoExpired() {
        orderService.cancelExpiredOrders() // должно пройти без ошибок
    }

    @Test
    @DisplayName("Оплата билета — успешный кейс")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsSuccess() {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().plusMinutes(10)
        orderRepository.save(order)

        val dto = orderService.payTickets(1L)
        assertEquals(OrderStatus.PAID, dto.status)
        val updatedTickets = ticketRepository.findAll()
        assertTrue(updatedTickets.any { it.status == TicketStatus.PAID })
    }

    @Test
    @DisplayName("Оплата билета — ошибка, если заказ просрочен")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsExpired() {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().minusMinutes(5)
        orderRepository.save(order)

        assertThrows<IllegalStateException> {
            orderService.payTickets(1L)
        }

        orderService.cancelExpiredOrders()
        val updated = orderRepository.findById(1L).get()
        assertEquals(OrderStatus.CANCELLED, updated.status)
    }

    @Test
    @DisplayName("Оплата билета — ошибка, если заказ уже оплачен")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsAlreadyPaid() {
        val order = orderRepository.findById(2L).get()
        assertEquals(OrderStatus.PAID, order.status)

        assertThrows<IllegalStateException> {
            orderService.payTickets(2L)
        }
    }

    @Test
    @DisplayName("Оплата билета — ошибка, если заказ не найден")
    fun payTicketsNotFound() {
        assertThrows<OrderNotFoundException> {
            orderService.payTickets(999L)
        }
    }

    @Test
    @DisplayName("Получение билетов по заказу — успешный кейс")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getTicketsPageByOrderIdSuccess() {
        val page = orderService.getTicketsPageByOrderId(orderId = 1L, page = 0, size = 10)

        assertEquals(1, page.totalElements)
        val ticket = page.content.first()
        assertEquals(1L, ticket.id)
        assertEquals(500, ticket.price)
        assertEquals(1, ticket.raw)
        assertEquals(1, ticket.number)
    }

    @Test
    @DisplayName("Получение билетов — пустая страница не вызывает внешний сервис")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getTicketsPageEmpty() {
        val page = orderService.getTicketsPageByOrderId(orderId = 1L, page = 1, size = 10)

        assertTrue(page.isEmpty)
        verifyNoInteractions(seatFeignClient)
    }

    @Test
    @DisplayName("Получение билетов — ошибка при разных showId в одном заказе")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert-multi-show.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getTicketsPageMixedShows() {
        assertThrows<IllegalStateException> {
            orderService.getTicketsPageByOrderId(orderId = 1L, page = 0, size = 10)
        }
    }
}
