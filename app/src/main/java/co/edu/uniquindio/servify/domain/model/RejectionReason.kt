package co.edu.uniquindio.servify.domain.model

/**
 * Motivos sugeridos al rechazar una publicación (mockup 23). El moderador puede elegir uno,
 * que rellena el campo de texto, y luego editarlo: el texto escrito es lo obligatorio.
 */
enum class RejectionReason(val label: String, val suggestedText: String) {
    MISLEADING_INFO(
        "Información engañosa",
        "La publicación contiene información engañosa sobre el servicio."
    ),
    WRONG_CATEGORY(
        "Categoría incorrecta",
        "La categoría seleccionada no corresponde al servicio ofrecido."
    ),
    INSUFFICIENT_INFO(
        "Información insuficiente",
        "La descripción no contiene información suficiente sobre el servicio."
    ),
    FORBIDDEN_CONTENT(
        "Contenido no permitido",
        "La publicación incluye contenido no permitido por las políticas de Servify."
    ),
    WRONG_LOCATION(
        "Ubicación incorrecta",
        "La ubicación indicada no coincide con la zona de cobertura del servicio."
    )
}
