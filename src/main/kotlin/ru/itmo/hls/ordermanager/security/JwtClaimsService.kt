package ru.itmo.hls.ordermanager.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import ru.itmo.hls.ordermanager.config.JwtProperties
import java.nio.charset.StandardCharsets

@Component
class JwtClaimsService(
    private val jwtProperties: JwtProperties
) {

    data class Claims(
        val userId: Long,
        val role: String,
        val theatreId: Long?
    )

    fun requireClaims(authHeader: String?): Claims {
        val token = extractBearerToken(authHeader)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header")
        return parseClaims(token)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")
    }

    private fun extractBearerToken(header: String?): String? {
        if (header.isNullOrBlank()) return null
        val prefix = "Bearer "
        if (!header.startsWith(prefix)) {
            return null
        }
        return header.removePrefix(prefix).trim()
    }

    private fun parseClaims(token: String): Claims? {
        val secretBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        val key = Keys.hmacShaKeyFor(secretBytes)
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            val userId = when (val rawId = claims["userId"]) {
                is Number -> rawId.toLong()
                is String -> rawId.toLongOrNull()
                else -> null
            } ?: return null
            val role = claims["role"]?.toString() ?: return null
            val theatreId = when (val rawId = claims["theatreId"]) {
                is Number -> rawId.toLong()
                is String -> rawId.toLongOrNull()
                else -> null
            }
            Claims(userId = userId, role = role, theatreId = theatreId)
        } catch (ex: Exception) {
            null
        }
    }
}
