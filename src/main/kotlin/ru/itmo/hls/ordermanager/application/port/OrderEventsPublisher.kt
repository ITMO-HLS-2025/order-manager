package ru.itmo.hls.ordermanager.application.port

import ru.itmo.hls.ordermanager.application.dto.OrderPaidEvent

interface OrderEventsPublisher {
    fun publishOrderPaid(event: OrderPaidEvent)
}
