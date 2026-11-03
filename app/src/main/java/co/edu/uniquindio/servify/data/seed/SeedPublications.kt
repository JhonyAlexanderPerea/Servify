package co.edu.uniquindio.servify.data.seed

import co.edu.uniquindio.servify.data.seed.SeedUsers.JHONY_ID
import co.edu.uniquindio.servify.data.seed.SeedUsers.JUAN_ID
import co.edu.uniquindio.servify.data.seed.SeedUsers.LUIS_ID
import co.edu.uniquindio.servify.data.seed.SeedUsers.MARIA_ID
import co.edu.uniquindio.servify.data.seed.SeedUsers.VALENTINA_ID
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus

/**
 * Publicaciones semilla (SOLO fase 2, en memoria). Cubren los cuatro estados para poder probar
 * "Mis publicaciones" con la cuenta de Jhony: 2 verificadas, 1 pendiente, 1 finalizada y 1 rechazada
 * (con motivo). Los demás usuarios tienen publicaciones verificadas para alimentar Home/Explorar.
 */
object SeedPublications {

    const val REJECTION_REASON = "La descripción no contiene información suficiente sobre el servicio."

    private const val DAY = 86_400_000L
    private const val T0 = 1_760_000_000_000L // base fija: los datos semilla no dependen del reloj

    val publications: List<Publication> = listOf(
        // ----- Jhony (cuenta de pruebas principal) -----
        Publication(
            id = "pub_jhony_plomeria", authorId = JHONY_ID,
            title = "Plomero residencial", category = "Hogar", subcategory = "Plomería",
            description = "Reparaciones, instalaciones y mantenimiento de redes hidráulicas en casas y apartamentos.",
            priceRange = "$80.000 – $150.000", zone = "Armenia y alrededores", city = "Armenia, Quindío",
            status = PublicationStatus.VERIFICADA,
            interestedUserIds = setOf(JUAN_ID, LUIS_ID, MARIA_ID, VALENTINA_ID),
            createdAtMillis = T0 - 30 * DAY, verifiedAtMillis = T0 - 29 * DAY
        ),
        Publication(
            id = "pub_jhony_electricidad", authorId = JHONY_ID,
            title = "Instalaciones eléctricas certificadas", category = "Hogar", subcategory = "Electricidad",
            description = "Cambio de tableros, cableado, tomas y revisión de cortos circuitos con garantía.",
            priceRange = "$100.000 – $250.000", zone = "Armenia, Calarcá y Montenegro", city = "Armenia, Quindío",
            status = PublicationStatus.VERIFICADA,
            interestedUserIds = setOf(JUAN_ID, MARIA_ID),
            createdAtMillis = T0 - 20 * DAY, verifiedAtMillis = T0 - 19 * DAY
        ),
        Publication(
            id = "pub_jhony_tutorias", authorId = JHONY_ID,
            title = "Tutorías de programación en Kotlin", category = "Educación", subcategory = "Tutorías",
            description = "Clases personalizadas de fundamentos de programación y desarrollo Android para estudiantes universitarios.",
            priceRange = "$30.000 – $50.000 por hora", zone = "Armenia (virtual y presencial)", city = "Armenia, Quindío",
            status = PublicationStatus.PENDIENTE,
            createdAtMillis = T0 - 1 * DAY
        ),
        Publication(
            id = "pub_jhony_computadores", authorId = JHONY_ID,
            title = "Reparación de computadores y laptops", category = "Tecnología", subcategory = "Reparación de equipos",
            description = "Formateo, mantenimiento, instalación de programas y reparación de equipos.",
            priceRange = "$50.000 – $120.000", zone = "Armenia y alrededores", city = "Armenia, Quindío",
            status = PublicationStatus.FINALIZADA,
            interestedUserIds = setOf(LUIS_ID),
            createdAtMillis = T0 - 60 * DAY, verifiedAtMillis = T0 - 59 * DAY, finalizedAtMillis = T0 - 10 * DAY
        ),
        Publication(
            id = "pub_jhony_paseo", authorId = JHONY_ID,
            title = "Paseo de mascotas", category = "Mascotas", subcategory = "Paseo de mascotas",
            description = "Paseos diarios.",
            priceRange = "$15.000 – $25.000", zone = "Armenia", city = "Armenia, Quindío",
            status = PublicationStatus.RECHAZADA, rejectionReason = REJECTION_REASON,
            createdAtMillis = T0 - 5 * DAY
        ),

        // ----- Otros usuarios (feed público) -----
        Publication(
            id = "pub_juan_mudanzas", authorId = JUAN_ID,
            title = "Mudanzas locales con camión", category = "Transporte", subcategory = "Mudanzas",
            description = "Traslado de enseres dentro del Eje Cafetero con cargadores incluidos.",
            priceRange = "$250.000 – $600.000", zone = "Armenia, Pereira y Manizales", city = "Armenia, Quindío",
            status = PublicationStatus.VERIFICADA,
            interestedUserIds = setOf(JHONY_ID, MARIA_ID, LUIS_ID),
            createdAtMillis = T0 - 40 * DAY, verifiedAtMillis = T0 - 39 * DAY
        ),
        Publication(
            id = "pub_luis_limpieza", authorId = LUIS_ID,
            title = "Limpieza profunda de apartamentos", category = "Hogar", subcategory = "Limpieza",
            description = "Limpieza de cocina, baños, vidrios y pisos. Productos incluidos.",
            priceRange = "$90.000 – $180.000", zone = "Pereira y Dosquebradas", city = "Pereira, Risaralda",
            status = PublicationStatus.VERIFICADA,
            interestedUserIds = setOf(JHONY_ID),
            createdAtMillis = T0 - 25 * DAY, verifiedAtMillis = T0 - 24 * DAY
        ),
        Publication(
            id = "pub_maria_veterinaria", authorId = MARIA_ID,
            title = "Veterinaria a domicilio", category = "Mascotas", subcategory = "Veterinaria",
            description = "Consulta general, vacunación y desparasitación para perros y gatos en tu casa.",
            priceRange = "$60.000 – $140.000", zone = "Manizales", city = "Manizales, Caldas",
            status = PublicationStatus.VERIFICADA,
            interestedUserIds = setOf(JHONY_ID, JUAN_ID, LUIS_ID, VALENTINA_ID),
            createdAtMillis = T0 - 50 * DAY, verifiedAtMillis = T0 - 49 * DAY
        ),
        Publication(
            id = "pub_valentina_soporte", authorId = VALENTINA_ID,
            title = "Soporte técnico para pymes", category = "Tecnología", subcategory = "Soporte técnico",
            description = "Configuración de redes, impresoras y copias de seguridad para pequeños negocios.",
            priceRange = "$70.000 – $200.000", zone = "Armenia", city = "Armenia, Quindío",
            status = PublicationStatus.PENDIENTE,
            createdAtMillis = T0 - 2 * DAY
        )
    )
}
