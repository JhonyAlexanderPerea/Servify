package co.edu.uniquindio.servify.domain.model

enum class PublicationStatus { PENDIENTE, VERIFICADA, RECHAZADA, FINALIZADA }

/**
 * Publicación de un servicio.
 *
 * - Los votos "Me interesa" son el conjunto de usuarios que votaron: eso garantiza
 *   "un voto por usuario" por construcción y permite alternar (agregar/quitar).
 * - [verifiedAtMillis] se llena la primera vez que un moderador verifica. Se conserva
 *   aunque luego pase a FINALIZADA, para poder contar "publicaciones verificadas"
 *   (un moderador puede finalizar una publicación que nunca fue verificada).
 */
data class Publication(
    val id: String,
    val authorId: String,
    val title: String,
    val category: String,
    val subcategory: String,
    val description: String,
    val priceRange: String,
    val zone: String,
    val city: String,
    val imageUrls: List<String> = emptyList(),
    val status: PublicationStatus = PublicationStatus.PENDIENTE,
    val rejectionReason: String? = null,
    val interestedUserIds: Set<String> = emptySet(),
    val createdAtMillis: Long = 0L,
    val verifiedAtMillis: Long? = null,
    val finalizedAtMillis: Long? = null
) {
    val interestedCount: Int get() = interestedUserIds.size

    val wasVerified: Boolean get() = verifiedAtMillis != null
}
