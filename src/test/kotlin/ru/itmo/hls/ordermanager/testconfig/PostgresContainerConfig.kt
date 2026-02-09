package ru.itmo.hls.ordermanager.testconfig

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
open class PostgresContainerConfig {

    companion object {
        @Container
        @JvmStatic
        private val postgres = PostgreSQLContainer("postgres:16")
            .withDatabaseName("order_db")
            .withUsername("postgres")
            .withPassword("postgres")

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
        }
    }

}
