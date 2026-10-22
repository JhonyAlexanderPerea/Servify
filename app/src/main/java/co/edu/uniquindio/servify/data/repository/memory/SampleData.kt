package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.RejectionReason
import co.edu.uniquindio.servify.domain.model.ReputationLevel
import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole

object SampleData {

    val currentUser = User(
        id = "u_jhony",
        name = "Jhony Ramírez",
        email = "jhony.ramirez@gmail.com",
        phone = "314 567 8901",
        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
        role = UserRole.PROVIDER,
        isVerified = true,
        trustIndexScore = 91,
        reputationLevel = ReputationLevel.GOLD,
        currentPoints = 1240,
        completedJobsCount = 23
    )

    val providerJuan = User(
        id = "u_juan",
        name = "Juan Carlos Mejía",
        email = "juan.mejia@gmail.com",
        phone = "310 456 7890",
        avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61",
        role = UserRole.PROVIDER,
        isVerified = true,
        trustIndexScore = 94,
        reputationLevel = ReputationLevel.SILVER,
        currentPoints = 890,
        completedJobsCount = 127
    )

    val providerLuis = User(
        id = "u_luis",
        name = "Luis Fernando Torres",
        email = "luis.torres@gmail.com",
        phone = "312 987 6543",
        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
        role = UserRole.PROVIDER,
        isVerified = true,
        trustIndexScore = 91,
        reputationLevel = ReputationLevel.GOLD,
        currentPoints = 1870,
        completedJobsCount = 89
    )

    val providerMaria = User(
        id = "u_maria",
        name = "María Alejandra Ríos",
        email = "maria.rios@gmail.com",
        phone = "315 234 5678",
        avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
        role = UserRole.PROVIDER,
        isVerified = true,
        trustIndexScore = 98,
        reputationLevel = ReputationLevel.PLATINUM,
        currentPoints = 4120,
        completedJobsCount = 203
    )

    val publicationsList = listOf(
        Publication(
            id = "pub_1",
            title = "Plomería residencial y comercial con atención de emergencias",
            description = "Ofrezco servicio profesional de plomería para residencias y comercios. Especialista en reparación e instalación de tuberías, tanques de reserva y detección de fugas invisibles.",
            categoryId = "hogar",
            subcategoryId = "Plomería",
            price = 80000.0,
            priceUnit = "por trabajo",
            images = listOf(
                "https://images.unsplash.com/photo-1504307651254-35680f356dfd",
                "https://images.unsplash.com/photo-1581092160607-ee22621dd758"
            ),
            provider = providerJuan,
            status = PublicationStatus.APPROVED,
            ratingAverage = 4.9,
            reviewsCount = 127,
            locationName = "Armenia y alrededores, Quindío",
            createdAt = System.currentTimeMillis() - 86400000L * 10
        ),
        Publication(
            id = "pub_2",
            title = "Electricista certificado RETIE y mantenimiento preventivo",
            description = "Instalaciones eléctricas, mantenimiento preventivo y correctivo, revisiones RETIE. Certificado por el SENA con más de 8 años de experiencia.",
            categoryId = "hogar",
            subcategoryId = "Electricidad",
            price = 60000.0,
            priceUnit = "por hora",
            images = listOf("https://images.unsplash.com/photo-1621905251189-08b45d6a269e"),
            provider = providerLuis,
            status = PublicationStatus.APPROVED,
            ratingAverage = 4.8,
            reviewsCount = 89,
            locationName = "Pereira y Dosquebradas, Risaralda",
            createdAt = System.currentTimeMillis() - 86400000L * 15
        ),
        Publication(
            id = "pub_3",
            title = "Clases particulares de matemáticas y preparación ICFES",
            description = "Refuerzo escolar, preparación para ICFES y exámenes universitarios. Niveles primaria, secundaria y pre-universitario con metodología personalizada.",
            categoryId = "educacion",
            subcategoryId = "Clases particulares",
            price = 40000.0,
            priceUnit = "por hora",
            images = listOf("https://images.unsplash.com/photo-1503676260728-1c00da094a0b"),
            provider = providerMaria,
            status = PublicationStatus.APPROVED,
            ratingAverage = 5.0,
            reviewsCount = 203,
            locationName = "Manizales, Caldas",
            createdAt = System.currentTimeMillis() - 86400000L * 20
        ),
        Publication(
            id = "pub_4",
            title = "Paseo y cuidado amoroso de mascotas a domicilio",
            description = "Paseos diarios, cuidado en casa y adiestramiento básico. Amor y responsabilidad garantizados con reporte fotográfico constante.",
            categoryId = "mascotas",
            subcategoryId = "Paseo de mascotas",
            price = 25000.0,
            priceUnit = "por paseo",
            images = listOf("https://images.unsplash.com/photo-1587300003388-59208cc962cb"),
            provider = currentUser,
            status = PublicationStatus.APPROVED,
            ratingAverage = 4.8,
            reviewsCount = 18,
            locationName = "Armenia, Quindío",
            createdAt = System.currentTimeMillis() - 86400000L * 5
        )
    )

    val commentsList = listOf(
        Comment(
            id = "c1",
            publicationId = "pub_1",
            author = User(
                id = "u_laura",
                name = "Laura Patricia Gómez",
                email = "laura@gmail.com",
                phone = "311 000 1122",
                avatarUrl = null
            ),
            rating = 5,
            text = "Excelente servicio. Llegó puntual a la hora pactada, identificó la fuga que llevaba semanas afectándome y la reparó de inmediato.",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        Comment(
            id = "c2",
            publicationId = "pub_1",
            author = User(
                id = "u_carlos",
                name = "Carlos Andrés Mendieta",
                email = "carlos@gmail.com",
                phone = "312 333 4455",
                avatarUrl = null
            ),
            rating = 5,
            text = "Muy profesional y transparente con los costos antes de iniciar la reparación. Lo recomiendo totalmente.",
            createdAt = System.currentTimeMillis() - 86400000L * 7
        )
    )

    val notificationsList = listOf(
        Notification(
            id = "n1",
            userId = "u_jhony",
            title = "Recordatorio de Cita Próxima",
            message = "Tu servicio de Plomería Residencial con Juan Carlos está programado para Hoy a las 2:00 PM.",
            type = NotificationType.APPOINTMENT_REMINDER,
            isRead = false,
            createdAt = System.currentTimeMillis() - 600000L,
            targetId = "req_1"
        ),
        Notification(
            id = "n2",
            userId = "u_jhony",
            title = "Publicación Verificada",
            message = "Tu anuncio de Paseo de mascotas fue revisado y aprobado exitosamente por la IA.",
            type = NotificationType.MODERATION_UPDATE,
            isRead = false,
            createdAt = System.currentTimeMillis() - 1800000L,
            targetId = "pub_4"
        ),
        Notification(
            id = "n3",
            userId = "u_jhony",
            title = "Insignia Desbloqueada",
            message = "Obtuviste la insignia 'Usuario Confiable' tras superar el 90% de Índice de Confianza.",
            type = NotificationType.ACHIEVEMENT_UNLOCKED,
            isRead = true,
            createdAt = System.currentTimeMillis() - 86400000L
        )
    )

    val requestsList = listOf(
        ServiceRequest(
            id = "req_1",
            publicationId = "pub_1",
            clientId = "u_jhony",
            providerId = "u_juan",
            scheduledDateTime = System.currentTimeMillis() + 7200000L,
            status = RequestStatus.ACCEPTED,
            note = "Por favor traer repuesto para llave de lavamanos",
            totalAmount = 80000.0,
            createdAt = System.currentTimeMillis() - 3600000L
        )
    )
}
