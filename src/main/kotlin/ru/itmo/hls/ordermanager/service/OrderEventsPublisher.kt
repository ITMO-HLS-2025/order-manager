package ru.itmo.hls.ordermanager.service

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import ru.itmo.hls.ordermanager.dto.OrderPaidEvent

@Service
class OrderEventsPublisher(
    private val rabbitTemplate: RabbitTemplate
) {

    private val log = LoggerFactory.getLogger(OrderEventsPublisher::class.java)

    fun publishOrderPaid(event: OrderPaidEvent) {
        rabbitTemplate.convertAndSend("hls.events", "order.paid", event)
        log.info("Published order.paid event for orderId={}", event.orderId)
    }
}
