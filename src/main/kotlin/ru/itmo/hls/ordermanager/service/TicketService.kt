package ru.itmo.hls.orderservice.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.itmo.hls.orderservice.entity.Ticket
import ru.itmo.hls.orderservice.entity.TicketStatus
import ru.itmo.hls.orderservice.repository.TicketRepository


@Service
class TicketService(
    private val ticketRepository: TicketRepository
)  {

    fun findAllBySeatIdInAndShowId(seatIds: List<Long>, showId: Long, status: TicketStatus): List<Ticket> {
        return ticketRepository.findAllBySeatIdInAndShowId(seatIds, showId, status)
    }

    fun findAllByOrder(orderId: Long): List<Ticket> {
        return ticketRepository.findAllByOrder(orderId)
    }
//
//    fun findTicketsByOrderId(orderId: Long, pageable: Pageable): Page<TicketProjection> {
//        return ticketRepository.findTicketsByOrderId(orderId, pageable)
//    }

    fun save(ticket: Ticket): Ticket {
        return ticketRepository.save(ticket)
    }
}