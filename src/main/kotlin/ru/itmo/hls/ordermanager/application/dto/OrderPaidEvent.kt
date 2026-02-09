package ru.itmo.hls.ordermanager.application.dto

data class OrderPaidEvent(
    val orderId: Long,
    val userId: Long,
    val tickets: List<OrderPaidTicket>
) {
    data class OrderPaidTicket(
        val ticketId: Long,
        val showId: Long,
        val seatId: Long?
    )
}
