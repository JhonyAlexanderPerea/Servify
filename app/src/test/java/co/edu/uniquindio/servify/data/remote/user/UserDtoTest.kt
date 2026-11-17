package co.edu.uniquindio.servify.data.remote.user

import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.ReputationLevel
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserDtoTest {

    private val user = User(
        id = "uid_1",
        name = "Ana",
        lastName = "Ruiz",
        email = "ana@servify.demo",
        phone = "3001234567",
        city = "Armenia",
        avatarUrl = "https://example.com/ana.png",
        role = UserRole.PROVIDER,
        isVerified = true,
        trustIndexScore = 75,
        reputationLevel = ReputationLevel.GOLD,
        currentPoints = 120,
        points = 120,
        completedJobsCount = 4,
        badges = setOf(Badge.PRIMERA_PUBLICACION, Badge.CIEN_PUNTOS),
        memberSinceMillis = 1_700_000_000_000L
    )

    @Test
    fun round_trip_user_dto_user_keeps_the_fields() {
        val roundTrip = UserDto.fromDomain(user).toDomain()

        assertEquals(user, roundTrip)
    }

    @Test
    fun unknown_role_falls_back_to_user() {
        val restored = UserDto(role = "SUPER_ADMIN").toDomain()

        assertEquals(UserRole.USER, restored.role)
    }

    @Test
    fun unknown_reputation_level_falls_back_to_bronze() {
        val restored = UserDto(reputationLevel = "MITICO").toDomain()

        assertEquals(ReputationLevel.BRONZE, restored.reputationLevel)
    }

    @Test
    fun unknown_badges_are_ignored() {
        val restored = UserDto(badges = listOf("PRIMERA_PUBLICACION", "NO_EXISTE")).toDomain()

        assertEquals(setOf(Badge.PRIMERA_PUBLICACION), restored.badges)
    }

    @Test
    fun blank_avatar_url_becomes_null() {
        val restored = UserDto(avatarUrl = "").toDomain()

        assertNull(restored.avatarUrl)
    }

    @Test
    fun dto_has_no_password_field() {
        val hasPassword = UserDto::class.java.declaredFields.any { it.name.contains("password", ignoreCase = true) }

        assertFalse(hasPassword)
    }

    @Test
    fun points_from_domain_are_written_to_the_dto() {
        val dto = UserDto.fromDomain(user)

        assertEquals(120, dto.points)
        assertTrue(dto.verified)
        assertEquals("PROVIDER", dto.role)
    }
}
