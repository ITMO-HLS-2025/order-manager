package ru.itmo.hls.ordermanager.domain.port

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import ru.itmo.hls.ordermanager.domain.model.Ticket
import ru.itmo.hls.ordermanager.domain.model.TicketStatus

interface TicketRepository {
    fun findAllBySeatIdInAndShowId(seatIds: Collection<Long>, showId: Long): List<Ticket>
    fun findAllByOrder(orderId: Long): List<Ticket>
    fun findAllByOrder(orderId: Long, pageable: Pageable): Page<Ticket>
    fun findSeatIdsByShowIdAndStatusIn(showId: Long, statuses: Collection<TicketStatus>): List<Long>
    fun save(ticket: Ticket): Ticket
}
