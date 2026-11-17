package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory

interface AiClassifierRepository {
    /**
     * Clasifica una publicación dentro del catálogo recibido.
     * Devuelve null si no pudo clasificarla; las fallas de red o de clave lanzan excepción.
     */
    suspend fun classify(
        title: String,
        description: String,
        catalog: List<CatalogCategory>
    ): AiClassification?
}
