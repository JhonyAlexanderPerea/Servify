package co.edu.uniquindio.servify.domain.model

/**
 * Categoría de servicios. Los colores se guardan como ARGB (Long) para que el dominio no dependa de Compose;
 * la UI los convierte a Color.
 */
data class Category(
    val id: String,
    val label: String,
    val iconName: String,
    val colorArgb: Long,
    val backgroundArgb: Long
)
