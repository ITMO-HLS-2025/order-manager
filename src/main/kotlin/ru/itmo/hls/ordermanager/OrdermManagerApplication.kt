package ru.itmo.hls.ordermanager

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableRabbit
class OrdermManagerApplication

fun main(args: Array<String>) {
    runApplication<OrdermManagerApplication>(*args)
}
