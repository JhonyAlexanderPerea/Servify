package co.edu.uniquindio.servify.features.service.model

import androidx.compose.ui.graphics.Color

data class TrustFactor(
    val title: String,
    val description: String,
    val isVerified: Boolean
)

data class CommentItemData(
    val id: String,
    val authorName: String,
    val authorInitials: String,
    val authorAvatarColor: Color,
    val rating: Double,
    val date: String,
    val commentText: String
)

data class ServiceDetailData(
    val id: String,
    val title: String,
    val category: String,
    val subcategory: String,
    val description: String,
    val priceRange: String,
    val hourlyPrice: String,
    val zone: String,
    val city: String,
    val images: List<String>,
    val providerName: String,
    val providerInitials: String,
    val providerColor: Color,
    val providerLevel: String,
    val providerMemberSince: String,
    val completedJobs: Int,
    val isVerified: Boolean,
    val trustIndex: Int,
    val rating: Double,
    val reviewCount: Int,
    val interestedCount: Int,
    val isFavorite: Boolean,
    val trustFactors: List<TrustFactor>,
    val comments: List<CommentItemData>
)

object ServiceDetailMockData {

    val sampleDetail = ServiceDetailData(
        id = "1",
        title = "Plomero residencial y comercial con atención de emergencias",
        category = "Hogar",
        subcategory = "Plomería",
        description = "Ofrezco servicio profesional de plomería para residencias y comercios. Especialista en reparación e instalación de tuberías de agua potable, desagües, grifería, tanques de reserva y detección de fugas invisibles con equipo tecnológico.\n\nContamos con más de 10 años de experiencia técnica comprobada, garantía por escrito de 6 meses en cada servicio realizado y atención prioritaria para emergencias las 24 horas del día en la ciudad y zonas aledañas.",
        priceRange = "$80.000 – $150.000",
        hourlyPrice = "$45.000 / hora",
        zone = "Armenia y municipios del Quindío",
        city = "Armenia, Quindío",
        images = listOf(
            "https://images.unsplash.com/photo-1504307651254-35680f356dfd",
            "https://images.unsplash.com/photo-1581092160607-ee22621dd758",
            "https://images.unsplash.com/photo-1621905251189-08b45d6a269e"
        ),
        providerName = "Juan Carlos Mejía",
        providerInitials = "JM",
        providerColor = Color(0xFF1A55E3),
        providerLevel = "Profesional",
        providerMemberSince = "Enero 2022",
        completedJobs = 127,
        isVerified = true,
        trustIndex = 94,
        rating = 4.9,
        reviewCount = 127,
        interestedCount = 34,
        isFavorite = true,
        trustFactors = listOf(
            TrustFactor(
                title = "Identidad y cédula verificada",
                description = "Documentación de identidad cotejada con la Registraduría Nacional.",
                isVerified = true
            ),
            TrustFactor(
                title = "Certificación laboral y antecedentes",
                description = "Certificado de antecedentes judiciales y competencias técnicas al día.",
                isVerified = true
            ),
            TrustFactor(
                title = "Garantía de servicio asegurada",
                description = "Respaldado por el fondo de protección al usuario Servify.",
                isVerified = true
            ),
            TrustFactor(
                title = "Calificación destacada > 4.8",
                description = "Evaluado satisfactoriamente por más de 100 clientes en la región.",
                isVerified = true
            )
        ),
        comments = listOf(
            CommentItemData(
                id = "c1",
                authorName = "Laura Patricia Gómez",
                authorInitials = "LG",
                authorAvatarColor = Color(0xFFEA4C89),
                rating = 5.0,
                date = "Hace 2 días",
                commentText = "Excelente servicio. Llegó puntual a la hora pactada, identificó la fuga que llevaba semanas afectándome y la reparó de inmediato. Dejó todo impecable."
            ),
            CommentItemData(
                id = "c2",
                authorName = "Carlos Andrés Mendieta",
                authorInitials = "CM",
                authorAvatarColor = Color(0xFF7B2FBE),
                rating = 4.8,
                date = "Hace 1 semana",
                commentText = "Muy profesional y transparente con los costos antes de iniciar la reparación. Lo recomiendo totalmente."
            ),
            CommentItemData(
                id = "c3",
                authorName = "Diana Marcela Ríos",
                authorInitials = "DR",
                authorAvatarColor = Color(0xFF0097A7),
                rating = 5.0,
                date = "Hace 2 semanas",
                commentText = "Gran trabajo en la instalación del calentador. Explicó detalladamente el mantenimiento adecuado."
            )
        )
    )
}
