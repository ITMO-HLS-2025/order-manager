package ru.itmo.hls.ordermanager.service

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class OrderCancelCommandListener(
    private val orderService: OrderService
) {

    private val log = LoggerFactory.getLogger(OrderCancelCommandListener::class.java)

    data class OrderCancelCheckCommand(val requestedAt: String? = null)

    @RabbitListener(queues = ["order-manager.cancel-check"])
    fun handle(command: OrderCancelCheckCommand) {
        log.info("Received order cancel check command requestedAt={}", command.requestedAt)
        orderService.cancelExpiredOrders()
    }
}
