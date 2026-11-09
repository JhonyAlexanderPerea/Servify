package co.edu.uniquindio.servify.domain.model

/** Factor individual del índice de confianza de un proveedor (ej. "Publicaciones verificadas"). */
data class TrustFactor(
    val title: String,
    val description: String,
    val isVerified: Boolean
)
