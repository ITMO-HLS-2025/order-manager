package ru.itmo.hls.ordermanager.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.hls.ordermanager.service.TicketService

@RestController
@RequestMapping("/inner/shows")
class InnerShowController(
    private val ticketService: TicketService
) {
    @GetMapping("/{showId}/occupied-seats")
    fun getOccupiedSeats(@PathVariable showId: Long): List<Long> {
        return ticketService.findOccupiedSeatIds(showId)
    }
}
