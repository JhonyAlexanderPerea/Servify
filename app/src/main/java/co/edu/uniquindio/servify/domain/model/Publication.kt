package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa una oferta o servicio publicado en Servify Marketplace.
 */
data class Publication(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val subcategoryId: String? = null,
    val price: Double,
    val priceUnit: String = "por hora",
    val images: List<String> = emptyList(),
    val provider: User,
    val status: PublicationStatus = PublicationStatus.APPROVED,
    val ratingAverage: Double = 0.0,
    val reviewsCount: Int = 0,
    val locationName: String,
    val isFavorite: Boolean = false,
    val interestedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
