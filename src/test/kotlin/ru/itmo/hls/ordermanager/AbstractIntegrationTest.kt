package ru.itmo.hls.ordermanager

import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.test.context.bean.override.mockito.MockitoBean
import ru.itmo.hls.ordermanager.client.SeatFeignClient
import ru.itmo.hls.ordermanager.client.ShowFeignClient
import ru.itmo.hls.ordermanager.service.PostgresContainerConfig
import ru.itmo.hls.ordermanager.dto.SeatPriceDto
import ru.itmo.hls.ordermanager.dto.ShowAccessDto
import ru.itmo.hls.ordermanager.dto.TheatreAccessDto
abstract class AbstractIntegrationTest : PostgresContainerConfig() {

    @MockitoBean
    private lateinit var seatFeignClient: SeatFeignClient
    @MockitoBean
    private lateinit var showFeignClient: ShowFeignClient

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

        Mockito.doAnswer { invocation ->
            val showId = invocation.getArgument<Long>(0)
            ShowAccessDto(
                id = showId,
                theatre = TheatreAccessDto(id = 1L)
            )
        }.`when`(showFeignClient).getShow(ArgumentMatchers.anyLong())
    }
}
