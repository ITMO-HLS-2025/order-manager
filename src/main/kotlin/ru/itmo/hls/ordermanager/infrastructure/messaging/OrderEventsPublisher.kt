package ru.itmo.hls.ordermanager.infrastructure.messaging

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import ru.itmo.hls.ordermanager.application.dto.OrderPaidEvent
import ru.itmo.hls.ordermanager.application.port.OrderEventsPublisher

@Service
class OrderEventsPublisher(
    private val rabbitTemplate: RabbitTemplate
) : OrderEventsPublisher {

    private val log = LoggerFactory.getLogger(OrderEventsPublisher::class.java)

    override fun publishOrderPaid(event: OrderPaidEvent) {
        rabbitTemplate.convertAndSend("hls.events", "order.paid", event)
        log.info("Published order.paid event for orderId={}", event.orderId)
    }
}
