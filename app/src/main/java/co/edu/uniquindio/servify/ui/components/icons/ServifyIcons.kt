package co.edu.uniquindio.servify.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Íconos del mockup que no existen en material-icons-extended.
 */
object ServifyIcons {

    /**
     * Material Symbols "shield_with_heart" (FILL 1).
     * Es el ícono del logo en Splash y Login.
     */
    val ShieldWithHeart: ImageVector by lazy {

        ImageVector.Builder(
            name = "ShieldWithHeart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {

            // El SVG original usa viewBox "0 -960 960 960"
            addGroup(translationY = 960f)

            addPath(
                pathData = PathParser()
                    .parsePathString(
                        "M480-320q102-92 131-129.5t29-74.5q0-36-26-62t-62-26q-21 0-40.5 8.5T480-580q-12-15-31-23.5t-41-8.5q-36 0-62 26t-26 62q0 19 5 35t22 37.5q17 21.5 48.5 52.5t84.5 79Zm0 240q-139-35-229.5-159.5T160-516v-244l320-120 320 120v244q0 152-90.5 276.5T480-80Z"
                    )
                    .toNodes(),
                fill = SolidColor(Color.Black)
            )

            clearGroup()

        }.build()
    }

    /**
     * Logo multicolor de Google (el mismo SVG del botón del mockup).
     * Usar con tint = Color.Unspecified.
     */
    val GoogleLogo: ImageVector by lazy {

        ImageVector.Builder(
            name = "GoogleLogo",
            defaultWidth = 18.dp,
            defaultHeight = 18.dp,
            viewportWidth = 18f,
            viewportHeight = 18f
        ).apply {

            addPath(
                pathData = PathParser()
                    .parsePathString(
                        "M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844a4.14 4.14 0 0 1-1.796 2.716v2.259h2.908c1.702-1.567 2.684-3.875 2.684-6.615Z"
                    )
                    .toNodes(),
                fill = SolidColor(Color(0xFF4285F4))
            )

            addPath(
                pathData = PathParser()
                    .parsePathString(
                        "M9 18c2.43 0 4.467-.806 5.956-2.184l-2.908-2.259c-.806.54-1.837.86-3.048.86-2.344 0-4.328-1.584-5.036-3.711H.957v2.332A8.997 8.997 0 0 0 9 18Z"
                    )
                    .toNodes(),
                fill = SolidColor(Color(0xFF34A853))
            )

            addPath(
                pathData = PathParser()
                    .parsePathString(
                        "M3.964 10.706A5.41 5.41 0 0 1 3.682 9c0-.593.102-1.17.282-1.706V4.962H.957A8.996 8.996 0 0 0 0 9c0 1.452.348 2.827.957 4.038l3.007-2.332Z"
                    )
                    .toNodes(),
                fill = SolidColor(Color(0xFFFBBC05))
            )

            addPath(
                pathData = PathParser()
                    .parsePathString(
                        "M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0A8.997 8.997 0 0 0 .957 4.958L3.964 7.29C4.672 5.163 6.656 3.58 9 3.58Z"
                    )
                    .toNodes(),
                fill = SolidColor(Color(0xFFEA4335))
            )

        }.build()
    }
}
