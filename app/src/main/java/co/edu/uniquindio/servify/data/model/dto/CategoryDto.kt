package co.edu.uniquindio.servify.data.model.dto

import androidx.annotation.Keep
import co.edu.uniquindio.servify.domain.model.Category

/**
 * Data Transfer Object (DTO) para la colección "categories" en Cloud Firestore.
 */
@Keep
data class CategoryDto(
    val id: String = "",
    val label: String = "",
    val iconName: String = "",
    val colorArgb: Long = 0xFF1A55E3,
    val backgroundArgb: Long = 0xFFD6E2FF
) {
    fun toDomain(): Category = Category(
        id = id,
        label = label,
        iconName = iconName,
        colorArgb = colorArgb,
        backgroundArgb = backgroundArgb
    )

    fun toDocument(): Map<String, Any?> = mapOf(
        "id" to id,
        "label" to label,
        "iconName" to iconName,
        "colorArgb" to colorArgb,
        "backgroundArgb" to backgroundArgb
    )

    companion object {
        fun fromDomain(c: Category): CategoryDto = CategoryDto(
            id = c.id,
            label = c.label,
            iconName = c.iconName,
            colorArgb = c.colorArgb,
            backgroundArgb = c.backgroundArgb
        )
    }
}
