package ru.itmo.hls.ordermanager.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.itmo.hls.ordermanager.entity.Ticket
import ru.itmo.hls.ordermanager.entity.TicketStatus
import ru.itmo.hls.ordermanager.repository.TicketRepository


@Service
class TicketService(
    private val ticketRepository: TicketRepository
)  {

    fun findAllBySeatIdInAndShowId(seatIds: List<Long>, showId: Long): List<Ticket> {
        return ticketRepository.findAllBySeatIdInAndShowId(seatIds, showId)
    }

    fun findAllByOrder(orderId: Long): List<Ticket> {
        return ticketRepository.findAllByOrder(orderId)
    }

    fun findAllByOrder(orderId: Long, pageable: Pageable): Page<Ticket> {
        return ticketRepository.findAllByOrder(orderId, pageable)
    }

    fun findOccupiedSeatIds(showId: Long): List<Long> {
        return ticketRepository.findSeatIdsByShowIdAndStatusIn(
            showId,
            listOf(TicketStatus.RESERVED, TicketStatus.PAID)
        )
    }

    fun save(ticket: Ticket): Ticket {
        return ticketRepository.save(ticket)
    }
}
