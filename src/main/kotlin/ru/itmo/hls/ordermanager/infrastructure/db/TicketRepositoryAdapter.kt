package ru.itmo.hls.ordermanager.infrastructure.db

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import ru.itmo.hls.ordermanager.domain.model.Ticket
import ru.itmo.hls.ordermanager.domain.model.TicketStatus
import ru.itmo.hls.ordermanager.domain.port.TicketRepository

@Repository
class TicketRepositoryAdapter(
    private val ticketJpaRepository: TicketJpaRepository
) : TicketRepository {

    override fun findAllBySeatIdInAndShowId(seatIds: Collection<Long>, showId: Long): List<Ticket> =
        ticketJpaRepository.findAllBySeatIdInAndShowId(seatIds, showId)

    override fun findAllByOrder(orderId: Long): List<Ticket> =
        ticketJpaRepository.findAllByOrder(orderId)

    override fun findAllByOrder(orderId: Long, pageable: Pageable): Page<Ticket> =
        ticketJpaRepository.findAllByOrder(orderId, pageable)

    override fun findSeatIdsByShowIdAndStatusIn(
        showId: Long,
        statuses: Collection<TicketStatus>
    ): List<Long> = ticketJpaRepository.findSeatIdsByShowIdAndStatusIn(showId, statuses)

    override fun save(ticket: Ticket): Ticket = ticketJpaRepository.save(ticket)
}
