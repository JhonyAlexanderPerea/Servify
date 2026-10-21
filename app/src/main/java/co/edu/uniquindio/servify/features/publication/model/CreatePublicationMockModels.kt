package co.edu.uniquindio.servify.features.publication.model

data class CategoryOption(
    val id: String,
    val name: String,
    val subcategories: List<String>
)

data class CreatePublicationUiState(
    val currentStep: Int = 1,
    val title: String = "",
    val titleError: String? = null,
    val selectedCategory: String = "",
    val selectedSubcategory: String = "",
    val description: String = "",
    val descriptionError: String? = null,
    val minPrice: String = "",
    val maxPrice: String = "",
    val cityZone: String = "Armenia, Quindío",
    val photoUrls: List<String> = emptyList(),
    val isAiAnalyzing: Boolean = false,
    val showAiSuggestion: Boolean = false,
    val aiSuggestedTitle: String = "",
    val aiSuggestedDescription: String = "",
    val aiSuggestedCategory: String = "",
    val aiSuggestedPrice: String = "",
    val isPublishedSuccess: Boolean = false
) {
    val isStep1Valid: Boolean
        get() = title.isNotBlank() && selectedCategory.isNotBlank() && selectedSubcategory.isNotBlank()

    val isStep2Valid: Boolean
        get() = description.isNotBlank() && minPrice.isNotBlank() && cityZone.isNotBlank()
}

object CreatePublicationMockData {

    val categoriesList = listOf(
        CategoryOption(
            id = "hogar",
            name = "Hogar",
            subcategories = listOf("Plomería", "Electricidad", "Limpieza", "Pintura", "Jardinería", "Carpintería")
        ),
        CategoryOption(
            id = "educacion",
            name = "Educación",
            subcategories = listOf("Clases particulares", "Idiomas", "Música", "Refuerzo escolar")
        ),
        CategoryOption(
            id = "mascotas",
            name = "Mascotas",
            subcategories = listOf("Paseo de mascotas", "Cuidado en casa", "Peluquería canina")
        ),
        CategoryOption(
            id = "tecnologia",
            name = "Tecnología",
            subcategories = listOf("Reparación de equipos", "Soporte técnico", "Diseño web", "Redes")
        ),
        CategoryOption(
            id = "transporte",
            name = "Transporte",
            subcategories = listOf("Mudanzas", "Mensajería express", "Acarreos")
        ),
        CategoryOption(
            id = "salud",
            name = "Salud y Bienestar",
            subcategories = listOf("Enfermería a domicilio", "Fisioterapia", "Masajes relajantes")
        )
    )

    val samplePhotos = listOf(
        "https://images.unsplash.com/photo-1504307651254-35680f356dfd",
        "https://images.unsplash.com/photo-1581092160607-ee22621dd758",
        "https://images.unsplash.com/photo-1621905251189-08b45d6a269e"
    )

    val filledState = CreatePublicationUiState(
        currentStep = 2,
        title = "Plomería y reparación de fuga de agua urgente",
        selectedCategory = "Hogar",
        selectedSubcategory = "Plomería",
        description = "Ofrezco servicio profesional de plomería para residencias y comercios. Especialista en reparación e instalación de tuberías, tanques de reserva y detección de fugas invisibles.",
        minPrice = "80000",
        maxPrice = "150000",
        cityZone = "Armenia y alrededores, Quindío",
        photoUrls = samplePhotos,
        showAiSuggestion = true,
        aiSuggestedTitle = "Plomería Residencial Certificada y Atención de Emergencias 24/7",
        aiSuggestedDescription = "Servicio profesional de plomería con 10 años de experiencia. Reparación de fugas, mantenimiento de tuberías y grifería con garantía de 6 meses.",
        aiSuggestedCategory = "Hogar • Plomería",
        aiSuggestedPrice = "$80.000 – $150.000 COP"
    )
}
