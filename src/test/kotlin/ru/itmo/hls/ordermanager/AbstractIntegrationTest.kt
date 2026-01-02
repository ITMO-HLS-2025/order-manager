package ru.itmo.hls.ordermanager

import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import ru.itmo.hls.ordermanager.client.SeatFeignClient
import ru.itmo.hls.ordermanager.dto.SeatPriceDto
import ru.itmo.hls.ordermanager.service.PostgresContainerConfig

abstract class AbstractIntegrationTest : PostgresContainerConfig() {

    @MockBean
    private lateinit var seatFeignClient: SeatFeignClient

    @BeforeEach
    fun stubSeatClient() {
        Mockito.doAnswer { invocation ->
            val seats = invocation.getArgument<List<Long>>(1)
            seats.map { seatId ->
                SeatPriceDto(
                    id = seatId,
                    raw = 1,
                    number = seatId.toInt(),
                    price = if (seatId == 3L) 900 else 500
                )
            }
        }.`when`(seatFeignClient).getSeat(
            ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyList()
        )
    }
}
