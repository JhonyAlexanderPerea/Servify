package co.edu.uniquindio.servify.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
fun cssLinearGradient(
    angleDegrees: Float,
    vararg colorStops: Pair<Float, Color>
): Brush = object : ShaderBrush() {

    override fun createShader(size: Size): Shader {

        val angle = Math.toRadians(angleDegrees.toDouble())

        val dx = sin(angle).toFloat()
        val dy = (-cos(angle)).toFloat()

        val length = abs(size.width * dx) + abs(size.height * dy)

        val center = Offset(size.width / 2f, size.height / 2f)
        val half = Offset(dx * length / 2f, dy * length / 2f)

        return LinearGradientShader(
            from = center - half,
            to = center + half,
            colors = colorStops.map { it.second },
            colorStops = colorStops.map { it.first },
            tileMode = TileMode.Clamp
        )
    }
}
