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
    val images: List<String> = emptyList(),
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
    val imageList: List<String> get() = if (images.isNotEmpty()) images else imageUrls
    val interestedSize: Int get() = if (interestedUserIds.isNotEmpty()) interestedUserIds.size else interestedCount
    val wasVerified: Boolean get() = verifiedAtMillis != null || status == PublicationStatus.VERIFICADA
}
