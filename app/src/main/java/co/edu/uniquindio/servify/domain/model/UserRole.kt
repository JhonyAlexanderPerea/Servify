package co.edu.uniquindio.servify.domain.model

/**
 * Rol del usuario dentro del ecosistema Servify Marketplace.
 */
enum class UserRole {
    /** Cliente ciudadano que busca y contrata servicios. */
    CLIENT,

    /** Proveedor o profesional independiente que publica servicios. */
    PROVIDER,

    /** Usuario genérico (usado en flujo de autenticación Fase 2). */
    USER,

    /** Administrador o moderador con permisos para revisar publicaciones. */
    MODERATOR
}
