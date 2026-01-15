package ru.itmo.hls.ordermanager.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.hls.ordermanager.client.SeatFeignClient
import ru.itmo.hls.ordermanager.dto.OrderDto
import ru.itmo.hls.ordermanager.dto.OrderPayload
import ru.itmo.hls.ordermanager.dto.SeatPriceDto
import ru.itmo.hls.ordermanager.dto.TicketDto
import ru.itmo.hls.ordermanager.entity.OrderStatus
import ru.itmo.hls.ordermanager.entity.TicketStatus
import ru.itmo.hls.ordermanager.exception.NotFreeSeatException
import ru.itmo.hls.ordermanager.exception.OrderNotFoundException
import ru.itmo.hls.ordermanager.mapper.toDto
import ru.itmo.hls.ordermanager.mapper.toEntity
import ru.itmo.hls.ordermanager.repository.OrderRepository
import java.time.LocalDateTime
import kotlin.collections.map
import kotlin.collections.sumOf
import kotlin.collections.toMutableList

@Service
@Transactional
open class OrderService(
    private val orderRepository: OrderRepository,
    private val ticketService: TicketService,
    private val seatFeignClient: SeatFeignClient
) {

    private val log: Logger = LoggerFactory.getLogger(OrderService::class.java)

    open fun reserveTickets(orderPayload: OrderPayload): OrderDto {
        log.info("Резервирование билетов для шоу id={} и мест {}", orderPayload.showId, orderPayload.seatIds)

        val seatsPrice = seatFeignClient.getSeat(orderPayload.showId, orderPayload.seatIds)
        val tickets = ticketService.findAllBySeatIdInAndShowId(
            seatsPrice.map { it.id },
            orderPayload.showId
        )
        if (tickets.isNotEmpty()) {
            log.warn("Невозможно создать заказ — места заняты: {}", orderPayload.seatIds)
            throw NotFreeSeatException("Невозможно создать заказ -- есть занятые места")
        }

        val ticketsEntity = seatsPrice.map { it.toEntity(orderPayload.showId) }.toMutableList()
        val sumOf = seatsPrice.sumOf { it.price }

        val order = orderPayload.toEntity(ticketsEntity, sumOf)
        order.tickets = ticketsEntity
        orderRepository.save(order)

        log.info("Заказ создан успешно: orderId={}, сумма={}", order.id, sumOf)
        return order.toDto()
    }

    @Scheduled(fixedRate = 60_000)
    open fun cancelExpiredOrders() {
        val now = LocalDateTime.now()
        val expired = orderRepository.findAllByStatusAndReservedAtBefore(OrderStatus.RESERVED, now)

        log.info("Автоотмена заказов. Найдено просроченных заказов: {}", expired.size)

        expired.forEach { order ->
            order.status = OrderStatus.CANCELLED
            order.tickets.forEach { ticket ->
                ticket.status = TicketStatus.CANCELLED
                ticketService.save(ticket)
            }
            orderRepository.save(order)
            log.info("Заказ {} автоматически отменён", order.id)
        }
    }

    @Transactional
    open fun payTickets(orderId: Long): OrderDto {
        log.info("Оплата заказа id={}", orderId)

        val order = orderRepository.findOrderById(orderId)
            ?: throw OrderNotFoundException("Заказ не найден: id=$orderId")

        order.tickets = ticketService.findAllByOrder(order.id).toMutableList()

        if (order.status == OrderStatus.PAID) {
            log.warn("Заказ {} уже оплачен", orderId)
            throw kotlin.IllegalStateException("Заказ уже оплачен")
        }

        val now = LocalDateTime.now()
        if (order.reservedAt != null && order.reservedAt!!.isBefore(now)) {
            order.status = OrderStatus.CANCELLED
            order.tickets.forEach {
                it.status = TicketStatus.CANCELLED
                ticketService.save(it)
            }
            orderRepository.save(order)
            log.warn("Время оплаты истекло — заказ {} автоматически отменён", orderId)
            throw kotlin.IllegalStateException("Время оплаты истекло — заказ автоматически отменён")
        }

        order.status = OrderStatus.PAID
        order.tickets.forEach { ticket ->
            ticket.status = TicketStatus.PAID
            ticketService.save(ticket)
        }

        val saved = orderRepository.save(order)
        saved.tickets = order.tickets

        log.info("Заказ {} оплачен успешно", orderId)
        return saved.toDto()
    }

    open fun getTicketsPageByOrderId(orderId: Long, page: Int, size: Int): Page<TicketDto> {
        log.info("Получение билетов по заказу id={}, страница={}, размер страницы={}", orderId, page, size)

        if (!orderRepository.existsById(orderId)) {
            log.warn("Заказ {} не найден при получении билетов", orderId)
            throw OrderNotFoundException("Заказ не найден: id=$orderId")
        }

        val pageable = PageRequest.of(page, size)
        val ticketPage = ticketService.findAllByOrder(orderId, pageable)
        if (ticketPage.isEmpty) {
            log.info("Билеты не найдены для заказа {}", orderId)
            return Page.empty<TicketDto>(pageable)
        }

        val showIds = ticketPage.content.map { it.showId }.distinct()
        if (showIds.size != 1) {
            throw IllegalStateException("Tickets from multiple shows in one order: $showIds")
        }
        val showId = showIds.first()
        val seatIds = ticketPage.content.map { ticket ->
            ticket.seatId ?: throw IllegalStateException("Ticket ${ticket.id} doesn't have seatId")
        }
        val seats = seatFeignClient.getSeat(showId, seatIds)
        val seatInfoById = seats.associateBy { it.id }

        log.info("Найдено билетов: {} для заказа {}", ticketPage.totalElements, orderId)
        return ticketPage.map { ticket ->
            val seatId = ticket.seatId
                ?: throw IllegalStateException("Ticket ${ticket.id} doesn't have seatId")
            val seat = seatInfoById[seatId]
                ?: throw IllegalStateException("Seat info not found for showId=$showId, seatId=$seatId")
            TicketDto(
                id = ticket.id,
                price = seat.price,
                raw = seat.raw,
                number = seat.number
            )
        }
    }

    open fun getOrderById(orderId: Long): OrderDto {
        log.info("Получение заказа id={}", orderId)

        val order = orderRepository.findOrderById(orderId)
            ?: throw OrderNotFoundException("Заказ не найден: id=$orderId")
        order.tickets = ticketService.findAllByOrder(order.id).toMutableList()
        return order.toDto()
    }
}
