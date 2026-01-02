package ru.itmo.hls.ordermanager

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<OrdermManagerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
