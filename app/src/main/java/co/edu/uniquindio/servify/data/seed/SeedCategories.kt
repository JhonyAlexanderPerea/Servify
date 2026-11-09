package co.edu.uniquindio.servify.data.seed

import co.edu.uniquindio.servify.domain.model.Category

/** Categorías del catálogo (Hogar, Educación, Mascotas, Tecnología, Transporte y Salud). */
object SeedCategories {

    val categories: List<Category> = listOf(
        Category("hogar", "Hogar", "home_repair_service", 0xFF1A55E3, 0xFFD6E2FF),
        Category("educacion", "Educación", "school", 0xFF7B2FBE, 0xFFEDE7F6),
        Category("mascotas", "Mascotas", "pets", 0xFFEA4C89, 0xFFFCE4EC),
        Category("tecnologia", "Tecnología", "computer", 0xFF0097A7, 0xFFE0F7FA),
        Category("transporte", "Transporte", "local_shipping", 0xFFF57C00, 0xFFFFF3E0),
        Category("salud", "Salud", "health_and_safety", 0xFF388E3C, 0xFFE8F5E9)
    )
}
