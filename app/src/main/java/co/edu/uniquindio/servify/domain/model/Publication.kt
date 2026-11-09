package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa una oferta o servicio publicado en Servify Marketplace.
 */
data class Publication(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String = "",
    val category: String = "",
    val subcategoryId: String? = null,
    val subcategory: String = "",
    val price: Double = 0.0,
    val priceRange: String = "",
    val priceUnit: String = "por hora",
    val imageUrls: List<String> = emptyList(),
    val provider: User = User(id = "u_default", name = "Proveedor Servify", email = ""),
    val authorId: String = "",
    val status: PublicationStatus = PublicationStatus.APPROVED,
    val ratingAverage: Double = 0.0,
    val reviewsCount: Int = 0,
    val locationName: String = "",
    val zone: String = "",
    val city: String = "",
    val isFavorite: Boolean = false,
    val interestedCount: Int = 0,
    val interestedUserIds: Set<String> = emptySet(),
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val createdAtMillis: Long = System.currentTimeMillis(),
    val verifiedAtMillis: Long? = null,
    val finalizedAtMillis: Long? = null
) {
    val interestedSize: Int get() = if (interestedUserIds.isNotEmpty()) interestedUserIds.size else interestedCount
    val wasVerified: Boolean get() = verifiedAtMillis != null || status == PublicationStatus.VERIFICADA

    /**
     * true cuando la publicación pertenece al usuario indicado (como autor o proveedor).
     * También compara el correo porque hay datos de demostración con ids distintos para la misma
     * persona; el correo evita que un usuario pueda solicitar su propio servicio por esa diferencia.
     */
    fun belongsTo(userId: String, userEmail: String? = null): Boolean =
        authorId == userId ||
            provider.id == userId ||
            (!userEmail.isNullOrBlank() && provider.email.equals(userEmail, ignoreCase = true))
}
