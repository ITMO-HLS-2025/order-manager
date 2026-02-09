package ru.itmo.hls.ordermanager.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var reservedAt: LocalDateTime? = null,

    var userId: Long,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.RESERVED,

    var sumPrice: Int,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "order_ticket",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "ticket_id")]
    )
    var tickets: MutableList<Ticket> = mutableListOf()
)
{
    constructor() : this(0, LocalDateTime.now(), null, 0, OrderStatus.RESERVED, 0)
}
