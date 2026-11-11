package co.edu.uniquindio.servify.data.model.dto

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.User

/**
 * Data Transfer Object (DTO) para la colección "publications" en Firebase Firestore.
 */
data class PublicationDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val categoryId: String = "",
    val category: String = "",
    val subcategoryId: String? = null,
    val subcategory: String = "",
    val price: Double = 0.0,
    val priceRange: String = "",
    val priceUnit: String = "por hora",
    val images: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val providerId: String = "",
    val providerName: String = "",
    val providerAvatar: String = "",
    val providerEmail: String = "",
    val authorId: String = "",
    val status: String = "APPROVED",
    val ratingAverage: Double = 0.0,
    val reviewsCount: Int = 0,
    val locationName: String = "",
    val zone: String = "",
    val city: String = "",
    val isFavorite: Boolean = false,
    val interestedCount: Int = 0,
    val interestedUserIds: List<String> = emptyList(),
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val createdAtMillis: Long = System.currentTimeMillis(),
    val verifiedAtMillis: Long? = null,
    val finalizedAtMillis: Long? = null
) {
    fun toDomain(): Publication {
        val finalImages = images.ifEmpty { imageUrls }
        val provId = providerId.ifBlank { authorId.ifBlank { "u_default" } }
        val authId = authorId.ifBlank { provId }
        val pubStatus = runCatching { PublicationStatus.valueOf(status) }.getOrDefault(PublicationStatus.APPROVED)

        return Publication(
            id = id,
            title = title,
            description = description,
            categoryId = categoryId,
            category = category,
            subcategoryId = subcategoryId,
            subcategory = subcategory,
            price = price,
            priceRange = priceRange,
            priceUnit = priceUnit,
            imageUrls = finalImages,
            provider = User(
                id = provId,
                name = providerName.ifBlank { "Proveedor Servify" },
                email = providerEmail,
                avatarUrl = providerAvatar.ifBlank { null }
            ),
            authorId = authId,
            status = pubStatus,
            ratingAverage = ratingAverage,
            reviewsCount = reviewsCount,
            locationName = locationName,
            zone = zone,
            city = city,
            isFavorite = isFavorite,
            interestedCount = interestedCount,
            interestedUserIds = interestedUserIds.toSet(),
            rejectionReason = rejectionReason,
            createdAt = if (createdAt > 0) createdAt else createdAtMillis,
            createdAtMillis = if (createdAtMillis > 0) createdAtMillis else createdAt,
            verifiedAtMillis = verifiedAtMillis,
            finalizedAtMillis = finalizedAtMillis
        )
    }

    fun toDocument(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "categoryId" to categoryId,
        "category" to category,
        "subcategoryId" to subcategoryId,
        "subcategory" to subcategory,
        "price" to price,
        "priceRange" to priceRange,
        "priceUnit" to priceUnit,
        "images" to images,
        "imageUrls" to imageUrls,
        "providerId" to providerId,
        "providerName" to providerName,
        "providerAvatar" to providerAvatar,
        "providerEmail" to providerEmail,
        "authorId" to authorId,
        "status" to status,
        "ratingAverage" to ratingAverage,
        "reviewsCount" to reviewsCount,
        "locationName" to locationName,
        "zone" to zone,
        "city" to city,
        "isFavorite" to isFavorite,
        "interestedCount" to interestedCount,
        "interestedUserIds" to interestedUserIds,
        "rejectionReason" to rejectionReason,
        "createdAt" to createdAt,
        "createdAtMillis" to createdAtMillis,
        "verifiedAtMillis" to verifiedAtMillis,
        "finalizedAtMillis" to finalizedAtMillis
    )

    companion object {
        fun fromDomain(pub: Publication): PublicationDto = PublicationDto(
            id = pub.id,
            title = pub.title,
            description = pub.description,
            categoryId = pub.categoryId,
            category = pub.category,
            subcategoryId = pub.subcategoryId,
            subcategory = pub.subcategory,
            price = pub.price,
            priceRange = pub.priceRange,
            priceUnit = pub.priceUnit,
            images = pub.imageUrls,
            imageUrls = pub.imageUrls,
            providerId = pub.provider.id,
            providerName = pub.provider.name,
            providerAvatar = pub.provider.avatarUrl ?: "",
            providerEmail = pub.provider.email,
            authorId = pub.authorId.ifBlank { pub.provider.id },
            status = pub.status.name,
            ratingAverage = pub.ratingAverage,
            reviewsCount = pub.reviewsCount,
            locationName = pub.locationName,
            zone = pub.zone,
            city = pub.city,
            isFavorite = pub.isFavorite,
            interestedCount = pub.interestedSize,
            interestedUserIds = pub.interestedUserIds.toList(),
            rejectionReason = pub.rejectionReason,
            createdAt = pub.createdAt,
            createdAtMillis = pub.createdAtMillis,
            verifiedAtMillis = pub.verifiedAtMillis,
            finalizedAtMillis = pub.finalizedAtMillis
        )
    }
}
