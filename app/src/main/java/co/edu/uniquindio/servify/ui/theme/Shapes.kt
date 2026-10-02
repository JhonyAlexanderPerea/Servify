package co.edu.uniquindio.servify.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // campos de texto (solo arriba), menús, skeletons, insignias de nivel
    small = RoundedCornerShape(8.dp),        // chips de filtro y de asistencia
    medium = RoundedCornerShape(16.dp),      // FAB, snackbar, banners, logo del login
    large = RoundedCornerShape(24.dp),       // tarjetas y contenedores grandes
    extraLarge = RoundedCornerShape(28.dp)   // diálogos y bottom sheets
)