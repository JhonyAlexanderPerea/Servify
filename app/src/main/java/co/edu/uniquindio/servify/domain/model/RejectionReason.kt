package co.edu.uniquindio.servify.domain.model

/**
 * Motivo por el cual un anuncio fue rechazado durante la moderación.
 */
enum class RejectionReason {
    /** Contenido inapropiado o lenguaje ofensivo. */
    INAPPROPRIATE_CONTENT,

    /** Precio engañoso o fuera del estándar del mercado. */
    MISLEADING_PRICE,

    /** Categoría o subcategoría incorrecta. */
    INVALID_CATEGORY,

    /** Publicación duplicada o spam. */
    SPAM,

    /** Otro motivo especificado en el reporte. */
    OTHER
}
