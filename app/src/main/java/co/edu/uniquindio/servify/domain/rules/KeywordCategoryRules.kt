package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import java.text.Normalizer

/** Clasificador local de respaldo: puntúa palabras clave cuando la IA no está disponible. */
object KeywordCategoryRules {

    private val keywordsBySubcategory: Map<String, List<String>> = mapOf(
        "plomeria" to listOf("plomer", "fuga", "tuberia", "grifo", "tanque", "sanitario"),
        "electricidad" to listOf("electric", "cable", "tomacorriente", "breaker", "cortocircuito", "iluminacion"),
        "limpieza" to listOf("limpieza", "aseo", "desinfeccion", "lavado"),
        "pintura" to listOf("pintura", "pintar", "estuco", "drywall"),
        "jardineria" to listOf("jardin", "poda", "cesped", "plantas"),
        "carpinteria" to listOf("carpinter", "madera", "mueble", "closet", "puerta"),
        "clases particulares" to listOf("clases", "tutoria", "profesor", "matematicas", "tareas"),
        "idiomas" to listOf("ingles", "idioma", "frances", "traduccion"),
        "musica" to listOf("musica", "guitarra", "piano", "canto"),
        "refuerzo escolar" to listOf("refuerzo", "colegio", "escolar", "primaria", "bachillerato"),
        "paseo de mascotas" to listOf("paseo", "pasear", "perro", "caminata"),
        "cuidado en casa" to listOf("cuidado", "guarderia", "hospedaje", "mascota"),
        "peluqueria canina" to listOf("peluqueria", "bano", "corte", "canina", "grooming"),
        "reparacion de equipos" to listOf("reparacion", "computador", "laptop", "portatil", "formateo", "celular", "pantalla"),
        "soporte tecnico" to listOf("soporte", "instalacion", "programas", "virus", "software"),
        "diseno web" to listOf("pagina web", "diseno web", "sitio web", "tienda virtual"),
        "redes" to listOf("redes", "wifi", "router", "cableado estructurado"),
        "mudanzas" to listOf("mudanza", "trasteo", "camion"),
        "mensajeria express" to listOf("mensajeria", "domicilio", "paquete", "entrega"),
        "acarreos" to listOf("acarreo", "escombros", "carga"),
        "enfermeria a domicilio" to listOf("enfermer", "inyeccion", "curaciones", "adulto mayor"),
        "fisioterapia" to listOf("fisioterapia", "rehabilitacion", "terapia fisica"),
        "masajes relajantes" to listOf("masaje", "relajante", "estres")
    )

    fun classify(
        title: String,
        description: String,
        catalog: List<CatalogCategory>
    ): AiClassification? {
        val normalizedTitle = normalize(title)
        val normalizedDescription = normalize(description)
        val normalizedCatalog = catalog.map { category ->
            category to category.subcategories.map { subcategory ->
                subcategory to normalize(subcategory)
            }
        }

        var bestCategory: String? = null
        var bestSubcategory: String? = null
        var bestScore = 0

        normalizedCatalog.forEach { (category, subcategories) ->
            subcategories.forEach { (subcategory, normalizedSubcategory) ->
                val keywords = keywordsBySubcategory[normalizedSubcategory] ?: return@forEach
                val score = keywords.sumOf { keyword ->
                    val inTitle = normalizedTitle.contains(keyword)
                    val inDescription = normalizedDescription.contains(keyword)
                    when {
                        inTitle && inDescription -> 3
                        inTitle -> 2
                        inDescription -> 1
                        else -> 0
                    }
                }
                if (score > bestScore) {
                    bestScore = score
                    bestCategory = category.name
                    bestSubcategory = subcategory
                }
            }
        }

        if (bestScore == 0 || bestCategory == null) return null

        return AiClassification(
            category = bestCategory,
            subcategory = bestSubcategory,
            confidence = (50 + 10 * bestScore).coerceAtMost(85)
        )
    }

    private fun normalize(text: String): String {
        val decomposed = Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
        return decomposed.replace(Regex("\\p{M}+"), "")
    }
}
