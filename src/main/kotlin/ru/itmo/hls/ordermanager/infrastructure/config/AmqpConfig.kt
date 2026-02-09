package ru.itmo.hls.ordermanager.infrastructure.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AmqpConfig {

    @Bean
    fun eventsExchange(): DirectExchange = DirectExchange("hls.events")

    @Bean
    fun commandsExchange(): DirectExchange = DirectExchange("hls.commands")

    @Bean
    fun cancelCheckQueue(): Queue = Queue("order-manager.cancel-check")

    @Bean
    fun cancelCheckBinding(commandsExchange: DirectExchange, cancelCheckQueue: Queue): Binding =
        BindingBuilder.bind(cancelCheckQueue).to(commandsExchange).with("order.check-cancel")

    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, converter: Jackson2JsonMessageConverter): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = converter
        return template
    }
}
