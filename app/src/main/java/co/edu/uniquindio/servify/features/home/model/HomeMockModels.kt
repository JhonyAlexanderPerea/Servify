package co.edu.uniquindio.servify.features.home.model

import androidx.compose.ui.graphics.Color

data class CategoryItem(
    val id: String,
    val label: String,
    val iconName: String,
    val color: Color,
    val backgroundColor: Color
)

data class AppointmentBannerItem(
    val id: String,
    val serviceTitle: String,
    val providerName: String,
    val dateTime: String,
    val location: String
)

data class ServiceItem(
    val id: String,
    val title: String,
    val category: String,
    val subcategory: String,
    val description: String,
    val priceRange: String,
    val zone: String,
    val city: String,
    val imageUrl: String,
    val providerName: String,
    val providerInitials: String,
    val providerColor: Color,
    val providerLevel: String,
    val isVerified: Boolean,
    val trustIndex: Int,
    val rating: Double,
    val reviewCount: Int,
    val interestedCount: Int,
    val isFavorite: Boolean = false,
    val nextAvailableSlot: String? = null
)

object HomeMockData {

    val currentUserCity = "Armenia, Quindío"
    val currentUserName = "Jhony"
    val currentUserInitials = "JR"

    val categories = listOf(
        CategoryItem(
            id = "hogar",
            label = "Hogar",
            iconName = "home_repair_service",
            color = Color(0xFF1A55E3),
            backgroundColor = Color(0xFFD6E2FF)
        ),
        CategoryItem(
            id = "educacion",
            label = "Educación",
            iconName = "school",
            color = Color(0xFF7B2FBE),
            backgroundColor = Color(0xFFEDE7F6)
        ),
        CategoryItem(
            id = "mascotas",
            label = "Mascotas",
            iconName = "pets",
            color = Color(0xFFEA4C89),
            backgroundColor = Color(0xFFFCE4EC)
        ),
        CategoryItem(
            id = "tecnologia",
            label = "Tecnología",
            iconName = "computer",
            color = Color(0xFF0097A7),
            backgroundColor = Color(0xFFE0F7FA)
        ),
        CategoryItem(
            id = "transporte",
            label = "Transporte",
            iconName = "local_shipping",
            color = Color(0xFFF57C00),
            backgroundColor = Color(0xFFFFF3E0)
        ),
        CategoryItem(
            id = "salud",
            label = "Salud",
            iconName = "health_and_safety",
            color = Color(0xFF388E3C),
            backgroundColor = Color(0xFFE8F5E9)
        )
    )

    val nextAppointment = AppointmentBannerItem(
        id = "apt_1",
        serviceTitle = "Plomería Residencial Urgente",
        providerName = "Juan Carlos Mejía",
        dateTime = "Hoy 2:00 PM",
        location = "Barrio Granada, Armenia"
    )

    val sampleServices = listOf(
        ServiceItem(
            id = "1",
            title = "Plomero residencial y comercial",
            category = "Hogar",
            subcategory = "Plomería",
            description = "Reparaciones, instalaciones y mantenimiento de tuberías. Servicio profesional con garantía de 6 meses en todos los trabajos realizados. Atención urgente disponible.",
            priceRange = "$80.000 – $150.000",
            zone = "Armenia y alrededores",
            city = "Armenia, Quindío",
            imageUrl = "https://images.unsplash.com/photo-1504307651254-35680f356dfd",
            providerName = "Juan Carlos Mejía",
            providerInitials = "JM",
            providerColor = Color(0xFF1A55E3),
            providerLevel = "Profesional",
            isVerified = true,
            trustIndex = 94,
            rating = 4.9,
            reviewCount = 127,
            interestedCount = 34,
            isFavorite = true,
            nextAvailableSlot = "Hoy 2:00 PM"
        ),
        ServiceItem(
            id = "2",
            title = "Electricista certificado RETIE",
            category = "Hogar",
            subcategory = "Electricidad",
            description = "Instalaciones eléctricas, mantenimiento preventivo y correctivo, revisiones RETIE. Certificado por el SENA con más de 8 años de experiencia.",
            priceRange = "$60.000 – $120.000",
            zone = "Pereira y Dosquebradas",
            city = "Pereira, Risaralda",
            imageUrl = "https://images.unsplash.com/photo-1621905251189-08b45d6a269e",
            providerName = "Luis Fernando Torres",
            providerInitials = "LT",
            providerColor = Color(0xFF7B2FBE),
            providerLevel = "Experto",
            isVerified = true,
            trustIndex = 91,
            rating = 4.8,
            reviewCount = 89,
            interestedCount = 21,
            isFavorite = false,
            nextAvailableSlot = "Mañana 9:00 AM"
        ),
        ServiceItem(
            id = "3",
            title = "Clases particulares de matemáticas y física",
            category = "Educación",
            subcategory = "Clases particulares",
            description = "Refuerzo escolar, preparación para ICFES y exámenes universitarios. Niveles primaria, secundaria y pre-universitario. Metodología personalizada.",
            priceRange = "$40.000 – $80.000",
            zone = "Manizales, zona urbana",
            city = "Manizales, Caldas",
            imageUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b",
            providerName = "María Alejandra Ríos",
            providerInitials = "MR",
            providerColor = Color(0xFFEA4C89),
            providerLevel = "Maestro",
            isVerified = true,
            trustIndex = 98,
            rating = 5.0,
            reviewCount = 203,
            interestedCount = 57,
            isFavorite = true,
            nextAvailableSlot = "Jueves 3:30 PM"
        ),
        ServiceItem(
            id = "4",
            title = "Paseo y cuidado amoroso de mascotas",
            category = "Mascotas",
            subcategory = "Paseo de mascotas",
            description = "Paseos diarios, cuidado en casa y adiestramiento básico. Amor y responsabilidad garantizados. Reporte fotográfico de cada salida.",
            priceRange = "$25.000 – $45.000",
            zone = "Armenia, Quindío",
            city = "Armenia, Quindío",
            imageUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb",
            providerName = "Valentina Ospina",
            providerInitials = "VO",
            providerColor = Color(0xFF0097A7),
            providerLevel = "Profesional",
            isVerified = true,
            trustIndex = 88,
            rating = 4.7,
            reviewCount = 64,
            interestedCount = 18,
            isFavorite = false,
            nextAvailableSlot = "Hoy 5:00 PM"
        )
    )
}
