package co.edu.uniquindio.servify.domain.model

/** Insignias del sistema de logros (pantalla 19). [title] y [requirement] son los textos de la UI. */
enum class Badge(val title: String, val requirement: String) {
    PRIMERA_PUBLICACION("Primera publicación", "Crea tu primera publicación"),
    PRIMERA_VERIFICADA("Primera verificada", "Consigue que un moderador verifique una de tus publicaciones"),
    DIEZ_VERIFICADAS("10 verificadas", "Consigue 10 publicaciones verificadas"),
    CIEN_PUNTOS("100 puntos", "Acumula 100 puntos Servify"),
    PUBLICACION_DESTACADA("Publicación destacada", "Ten la publicación verificada con más votos “Me interesa” (mínimo 5)"),
    DIEZ_COMENTARIOS("10 comentarios", "Escribe 10 comentarios en publicaciones"),
    USUARIO_CONFIABLE("Usuario confiable", "Ten 3 publicaciones verificadas y ninguna rechazada")
}
