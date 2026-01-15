package ru.itmo.hls.ordermanager.controller

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import ru.itmo.hls.ordermanager.service.PostgresContainerConfig

@SpringBootTest(classes = [PaginationValidator::class])
@TestPropertySource(properties = ["spring.application.pagination.max-size=5"])
class PaginationValidatorTest : PostgresContainerConfig() {

    @Autowired
    private lateinit var paginationValidator: PaginationValidator

    @Test
    fun validateSizeAcceptsPositiveValues() {
        assertDoesNotThrow { paginationValidator.validateSize(1) }
        assertDoesNotThrow { paginationValidator.validateSize(5) }
    }

    @Test
    fun validateSizeRejectsTooLarge() {
        assertThrows(IllegalArgumentException::class.java) {
            paginationValidator.validateSize(6)
        }
    }
}
