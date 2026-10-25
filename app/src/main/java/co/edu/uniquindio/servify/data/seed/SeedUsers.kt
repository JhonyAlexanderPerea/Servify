package co.edu.uniquindio.servify.data.seed

import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole

/**
 * Datos semilla de usuarios (SOLO fase 2, en memoria).
 * Los ids son fijos para que las publicaciones semilla puedan referenciar a sus autores (authorId).
 * Los puntos son coherentes con los umbrales de [co.edu.uniquindio.servify.domain.model.Level].
 */
object SeedUsers {
    const val JHONY_ID = "user_jhony"
    const val JUAN_ID = "user_juan"
    const val LUIS_ID = "user_luis"
    const val MARIA_ID = "user_maria"
    const val VALENTINA_ID = "user_valentina"
    const val MODERATOR_ID = "mod_1"

    const val MODERATOR_EMAIL = "moderador@servify.co"

    val users: List<User> = listOf(
        // 1.240 puntos = Experto (igual que el mockup del perfil)
        User(
            id = JHONY_ID, name = "Jhony", lastName = "Ramírez",
            email = "jhony.ramirez@gmail.com", city = "Armenia, Quindío",
            points = 1_240,
            badges = setOf(
                Badge.PRIMERA_PUBLICACION, Badge.PRIMERA_VERIFICADA, Badge.DIEZ_VERIFICADAS,
                Badge.CIEN_PUNTOS, Badge.USUARIO_CONFIABLE
            ),
            memberSinceMillis = 1_640_995_200_000L // 01-ene-2022
        ),
        User(
            id = JUAN_ID, name = "Juan Carlos", lastName = "Mejía",
            email = "juan.mejia@servify.demo", city = "Armenia, Quindío",
            points = 420, badges = setOf(Badge.PRIMERA_PUBLICACION, Badge.PRIMERA_VERIFICADA, Badge.CIEN_PUNTOS),
            memberSinceMillis = 1_641_000_000_000L
        ),
        User(
            id = LUIS_ID, name = "Luis Fernando", lastName = "Torres",
            email = "luis.torres@servify.demo", city = "Pereira, Risaralda",
            points = 900, badges = setOf(Badge.PRIMERA_PUBLICACION, Badge.PRIMERA_VERIFICADA, Badge.CIEN_PUNTOS),
            memberSinceMillis = 1_650_000_000_000L
        ),
        User(
            id = MARIA_ID, name = "María Alejandra", lastName = "Ríos",
            email = "maria.rios@servify.demo", city = "Manizales, Caldas",
            points = 1_850,
            badges = setOf(
                Badge.PRIMERA_PUBLICACION, Badge.PRIMERA_VERIFICADA, Badge.CIEN_PUNTOS, Badge.USUARIO_CONFIABLE
            ),
            memberSinceMillis = 1_620_000_000_000L
        ),
        User(
            id = VALENTINA_ID, name = "Valentina", lastName = "Ospina",
            email = "valentina.ospina@servify.demo", city = "Armenia, Quindío",
            points = 380, badges = setOf(Badge.PRIMERA_PUBLICACION, Badge.CIEN_PUNTOS),
            memberSinceMillis = 1_660_000_000_000L
        ),
        User(
            id = MODERATOR_ID, name = "Moderación", lastName = "Servify",
            email = MODERATOR_EMAIL, city = "Armenia, Quindío",
            role = UserRole.MODERATOR, memberSinceMillis = 1_600_000_000_000L
        )
    )

    /**
     * email -> contraseña. Claves en texto plano: aceptable solo porque es una simulación en memoria.
     * El email de Jhony y su clave coinciden con los valores precargados en LoginUiState.
     */
    val credentials: Map<String, String> = mapOf(
        "jhony.ramirez@gmail.com" to "12345678",
        "juan.mejia@servify.demo" to "Servify2026",
        "juan.mejia@gmail.com" to "Servify2026",
        "luis.torres@servify.demo" to "Servify2026",
        "luis.torres@gmail.com" to "Servify2026",
        "maria.rios@servify.demo" to "Servify2026",
        "maria.rios@gmail.com" to "Servify2026",
        "valentina.ospina@servify.demo" to "Servify2026",
        MODERATOR_EMAIL to "Moderador2026"
    )
}
